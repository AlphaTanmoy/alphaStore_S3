package com.alphaStore.alphaS3.security

import com.alphaStore.config_server.KeywordsAndConstants
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_BLOCKED
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_BLOCKED_DESCRIPTION
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_EXPIRED
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_EXPIRED_DESCRIPTION
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_NOT_VALID
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_NOT_VALID_DESCRIPTION
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_REFRESHED
import com.alphaStore.config_server.KeywordsAndConstants.TOKEN_REFRESHED_DESCRIPTION
import com.alphaStore.alphaS3.JwtAuthenticationToken
import com.alphaStore.alphaS3.contract.aggregator.JWTBlackListRepoAggregator
import com.alphaStore.alphaS3.contract.aggregator.JwtTokenUseLogRepoAggregator
import com.alphaStore.alphaS3.entity.JWTBlackList
import com.alphaStore.alphaS3.enums.AccessRole
import com.alphaStore.alphaS3.enums.UserType
import com.alphaStore.alphaS3.error.BadRequestException
import com.alphaStore.alphaS3.error.BadRequestExceptionThrowable
import com.alphaStore.alphaS3.error.UnAuthorizedExceptionThrowable
import com.alphaStore.alphaS3.utils.*
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.ZonedDateTime

@Component
class AuthenticationManagerJWT (
    private val jwtUtilMaster: JwtUtilMaster,
    private val jwtBlackListRepoAggregator: JWTBlackListRepoAggregator,
    private val encryptionMaster: EncryptionMaster,
    private val encodingUtil: EncodingUtil,
    private val jwtTokenUseLogRepoAggregator: JwtTokenUseLogRepoAggregator
): ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val authToken = (authentication as JwtAuthenticationToken).token
        verifyJwt(
            authToken.replace(
                KeywordsAndConstants.TOKEN_PREFIX,
                ""
            ),
            authentication,
            authentication.trackingId,
        )
        val toReturnUserNamePasswordAuthenticationToken = JwtAuthenticationToken(
            authentication.token,
            authentication.httpMethod,
            authentication.path,
            authentication.httpHeaders,
            authentication.trackingId
        )
        return Mono.just(toReturnUserNamePasswordAuthenticationToken)
    }

    private fun verifyJwt(
        authToken: String,
        jwtAuthenticationToken: JwtAuthenticationToken,
        trackingId: String,
    ): Boolean {
        try {
            val bodyOptional = jwtUtilMaster.getBody(
                authToken,
            )
            if (bodyOptional.isEmpty) {
                throw BadRequestException(
                    errorMessage = "Failed to parse data"
                )
            }
            val body = bodyOptional.get()
            checkIfTokenIsBlocked(
                body["id"].toString(),
                body["createdAt"].toString()
            )
            if (body["sub"] == KeywordsAndConstants.REFRESH_TOKEN_SUB) {
                if (jwtAuthenticationToken.path.contains("refreshToken")) {
                    return checkRefreshTokenAuthorizations(
                        body["for"].toString()
                    )
                } else {
                    throw BadRequestExceptionThrowable(
                        "Refresh token can only be used to refresh your current access token"
                    )
                }
            } else {
                if (jwtAuthenticationToken.path.contains("refreshToken")) {
                    throw BadRequestExceptionThrowable(
                        "Need a refresh token. You have provided a access token."
                    )
                }
                checkIfTokenRefreshed(
                    jwtAuthenticationToken.token.split(".")[2]
                )

                return checkJwtAuthorizations(
                    jwtAuthenticationToken = jwtAuthenticationToken,
                    userType = UserType.valueOf(body["type"].toString()),
                    accessRole = AccessRole.valueOf(body["accessRole"].toString()),
                    userId = body["id"].toString(),
                    subject = body["sub"].toString(),
                )
            }
        } catch (expiredJwtException: ExpiredJwtException) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_EXPIRED_DESCRIPTION,
                code = TOKEN_EXPIRED
            )
        } catch (signatureException: SignatureException) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_NOT_VALID_DESCRIPTION,
                code = TOKEN_NOT_VALID
            )
        } catch (ex: Exception) {
            throw BadRequestExceptionThrowable(errorMessage = "Unable to process")
        }
    }

    @Throws(UnAuthorizedExceptionThrowable::class)
    private fun checkIfTokenIsBlocked(
        userId: String,
        createdAt: String
    ) {
        var blocked = false
        val jwtBlackLists: java.util.ArrayList<JWTBlackList> = java.util.ArrayList()
        jwtBlackLists.addAll(
            ArrayList(jwtBlackListRepoAggregator.findByMerchantIdAndDataStatus(userId))
        )
        val createdOptional = DateUtil.getZonedDateTimeFromStringUsingIsoFormatServerTimeZone(
            encryptionMaster.decrypt(
                encodingUtil.decode(
                    createdAt
                ),
            )
        )
        if (createdOptional.isEmpty) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_BLOCKED_DESCRIPTION,
                code = TOKEN_BLOCKED
            )
        }
        val createdAtOffsetDateTime: ZonedDateTime = createdOptional.get()
        jwtBlackLists.forEach { jwtBlackList ->
            if (
                !blocked &&
                createdAtOffsetDateTime.isBefore(jwtBlackList.createdDate)
            ) {
                blocked = true
            }
        }
        if (blocked) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_BLOCKED_DESCRIPTION,
                code = TOKEN_BLOCKED
            )
        }
    }

    private fun checkRefreshTokenAuthorizations(jwtAuthenticationToken: String): Boolean {
        if (
            checkIfTokenUsed(
                jwtAuthenticationToken.split(".")[2]
            )
        ) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_BLOCKED_DESCRIPTION,
                code = TOKEN_BLOCKED
            )
        }
        return true
    }

    private fun checkIfTokenUsed(jwtHash: String): Boolean {
        jwtTokenUseLogRepoAggregator.findByTokenHashAndDataStatus(
            jwtHash,
        ).let {
            return it.isNotEmpty()
        }
    }

    private fun checkIfTokenRefreshed(jwtHash: String): Boolean {
        if (
            checkIfTokenUsed(jwtHash)
        ) {
            throw UnAuthorizedExceptionThrowable(
                errorMessage = TOKEN_REFRESHED_DESCRIPTION,
                code = TOKEN_REFRESHED
            )
        }
        return true
    }

    private fun checkJwtAuthorizations(
        jwtAuthenticationToken: JwtAuthenticationToken,
        accessRole: AccessRole,
        userType: UserType,
        userId: String,
        subject: String,
    ): Boolean {
        if (subject != KeywordsAndConstants.TOKEN_TIRE_ONE) {
            throw BadRequestExceptionThrowable("Invalid token subject")
        }

        val jwtAuthorities = jwtAuthenticationToken.authorities.map { it.authority }
        val hasValidRole = jwtAuthorities.contains(accessRole.name)

        if (!hasValidRole) {
            throw BadRequestExceptionThrowable("Unauthorized: Missing required role")
        }

        return true
    }

}