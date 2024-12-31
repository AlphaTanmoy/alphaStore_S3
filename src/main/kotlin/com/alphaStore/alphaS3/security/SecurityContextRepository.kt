package com.alphaStore.alphaS3.security


import com.alphaStore.config_server.KeywordsAndConstants
import com.alphaStore.config_server.KeywordsAndConstants.HEADER_API_KEY
import com.alphaStore.config_server.KeywordsAndConstants.HEADER_TRACKING_ID
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_OR_API_KEY_REQUIRED
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_OR_API_KEY_REQUIRED_DESCRIPTION
import com.alphaStore.alphaS3.JwtAuthenticationToken
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.error.UnAuthorizedExceptionThrowable
import com.alphaStore.alphaS3.utils.EncodingUtil
import com.alphaStore.alphaS3.utils.JwtUtilMaster
import com.alphaStore.alphaS3.utils.ValidateForUUID
import com.alphaStore.alphaS3.utils.ipmonitor.IPResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(
    private val authenticationManagerJWT: AuthenticationManagerJWT,
    private val jwtUtilMaster: JwtUtilMaster,
    private val encodingUtil: EncodingUtil
) : ServerSecurityContextRepository {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SecurityContextRepository::class.java)
    }

    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        println(">>>>>>>>>>>>>>>>>>")
        val ip = IPResolver.resolve(serverWebExchange)
        logger.info("Got request from $ip")

        val path = serverWebExchange.request.path
        logger.info("Request path $path")

        if (path.toString().contains("actuator")) {
            logger.info("Got request for actuator")
            logger.info("Going to verify for the auth")
            val basicAuthHeader = serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            basicAuthHeader?.let { basicAuthHeaderPositive ->
                if (!basicAuthHeaderPositive.contains("Basic")) {
                    logger.info("Basic auth not correct. failed to find Basic string in auth string")
                    throw UnAuthorizedExceptionThrowable()
                }
                val basicAuthHeaderPositiveHash = basicAuthHeaderPositive.replace("Basic ", "")
                if (
                    basicAuthHeaderPositiveHash == encodingUtil.encode(
                        "${KeywordsAndConstants.MICRO_SERVICE_USER_NAME}:${KeywordsAndConstants.MICRO_SERVICE_PASSWORD}"
                    )
                ) {
                    logger.info("Incorrect basic auth for actuator access")
                    return Mono.empty()
                }
                throw UnAuthorizedExceptionThrowable()
            }
            logger.info("Failed to find the basic auth header")
            throw UnAuthorizedExceptionThrowable()
        }

        if (
            path.toString() == "/" ||
            path.toString().contains("api-docs") ||
            path.toString().contains("swagger-ui") ||
            path.toString().contains("favicon.ico") ||
            path.toString().contains("/admin/hmac")
        ) {
            logger.info("got a request on swagger. letting through")
            return Mono.empty()
        }

        val jwtAuthToken = jwtUtilMaster.getJwtAuth(serverWebExchange)
        val token = jwtAuthToken ?: run { "" }

        var foundNoAuthApisInPath = !path.toString().startsWith("/api")
        KeywordsAndConstants.NO_AUTH_APIS.split(",").forEach { api ->
            if (
                !foundNoAuthApisInPath &&
                (
                        path.toString().contains(api, ignoreCase = true)
                        )
            ) {
                foundNoAuthApisInPath = true
            }
        }
        if (foundNoAuthApisInPath) {
            return Mono.empty()
        }

        KeywordsAndConstants.APIS_ALLOWED_WITH_BOTH_KEY_AND_TOKEN.split(",").forEach { api ->
            if (!foundNoAuthApisInPath && path.toString().contains(api, ignoreCase = true)) {
                foundNoAuthApisInPath = true
            }
        }
        if (foundNoAuthApisInPath) {
            if (serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) == null &&
                serverWebExchange.request.headers.getFirst(HEADER_API_KEY) == null
            ) {
                throw UnAuthorizedExceptionThrowable(
                    errorMessage = TOKEN_OR_API_KEY_REQUIRED_DESCRIPTION,
                    code = TOKEN_OR_API_KEY_REQUIRED
                )
            }
            if (serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) == null &&
                serverWebExchange.request.headers.getFirst(HEADER_API_KEY) != null
            ) {
                val apiKey = serverWebExchange.request.headers.getFirst(HEADER_API_KEY)
                apiKey?.let {
                    try {
                        ValidateForUUID.check(apiKey, "Api Key")
                    } catch (_: Exception) {
                        throw BadRequestException(
                            errorMessage = "Please provide valid api key"
                        )
                    }
                }
                return Mono.empty()
            }
        }

        val auth: Authentication = JwtAuthenticationToken(
            jwtUtilMaster.getJwtAuth(serverWebExchange, mustReturnTokenOrElseCrash = true)!!,
            serverWebExchange.request.method.name(),
            path.toString(),
            serverWebExchange.request.headers,
            serverWebExchange.request.headers.getFirst(HEADER_TRACKING_ID) ?: ""
        )

        return this.authenticationManagerJWT.authenticate(auth).map { authentication ->
            SecurityContextImpl(authentication)
        }
    }

}