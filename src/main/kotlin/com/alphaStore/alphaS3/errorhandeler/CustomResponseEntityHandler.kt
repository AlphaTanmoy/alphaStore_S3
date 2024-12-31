package com.alphaStore.alphaS3.errorhandeler

import com.alphaStore.config_server.KeywordsAndConstants
import com.alphaStore.alphaS3.enums.ResponseType
import com.alphaStore.alphaS3.error.*
import com.alphaStore.alphaS3.reqres.GenericResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.NotFoundException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.ConnectException

@Configuration
@Order(-2)
class CustomResponseEntityHandler(private val objectMapper: ObjectMapper) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        ex.printStackTrace()
        var message = "bad request."
        when (ex) {
            is UnAuthorizedExceptionThrowable, is UnAuthorizedException, is ForbiddenExceptionThrowable, is ForbiddenException, is BadRequestExceptionThrowable, is BadRequestException -> {
                val genericResponse = GenericResponse()
                genericResponse.responseType = ResponseType.FAIL
                when (ex) {
                    is UnAuthorizedExceptionThrowable -> {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = ex.code
                        genericResponse.type = ex.errorMessage
                    }

                    is UnAuthorizedException -> {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = ex.code
                        genericResponse.type = ex.errorMessage
                    }

                    is ForbiddenExceptionThrowable -> {
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = ex.code ?: HttpStatus.FORBIDDEN.value()
                    }

                    is ForbiddenException -> {
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = ex.code ?: HttpStatus.FORBIDDEN.value()
                    }

                    is BadRequestExceptionThrowable -> {
                        exchange.response.statusCode = HttpStatus.BAD_REQUEST
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = HttpStatus.BAD_REQUEST.value()
                    }

                    is BadRequestException -> {
                        exchange.response.statusCode = HttpStatus.BAD_REQUEST
                        genericResponse.message = ex.errorMessage
                        genericResponse.code = HttpStatus.BAD_REQUEST.value()
                    }
                }
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            genericResponse
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }

            is TooManyRequestExceptionThrowable -> {
                exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            GenericResponse(
                                message = "Too many request",
                                code = HttpStatus.TOO_MANY_REQUESTS.value()
                            )
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }

            is MoreAuthorizationRequiredThrowable -> {
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            GenericResponse(
                            )
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }

            is NotFoundException, is ConnectException -> {
                exchange.response.statusCode = HttpStatus.SERVICE_UNAVAILABLE
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            GenericResponse(
                                code = KeywordsAndConstants.SERVICE_NOT_AVAILABLE,
                                type = KeywordsAndConstants.SERVICE_NOT_AVAILABLE_DESCRIPTION,
                                message = KeywordsAndConstants.SERVICE_NOT_AVAILABLE_DESCRIPTION,
                                responseType = ResponseType.FAIL,
                            )
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }

            is ServiceUnderMaintenanceThrowable -> {
                exchange.response.statusCode = HttpStatus.SERVICE_UNAVAILABLE
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            GenericResponse(
                                code = KeywordsAndConstants.SERVICE_UNDER_MAINTENANCE,
                                type = ex.errorMessage,
                                message = ex.errorMessage,
                                responseType = ResponseType.FAIL,
                            )
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }

            else -> {
                var gotMessage = false
                ex.cause?.message?.let {
                    message = it
                    gotMessage = true
                }
                if (!gotMessage)
                    ex.message?.let { messageFromException ->
                        message = messageFromException
                    }
                exchange.response.statusCode = HttpStatus.BAD_REQUEST
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                val buffer = exchange
                    .response
                    .bufferFactory()
                    .wrap(
                        objectMapper.writeValueAsBytes(
                            GenericResponse(
                                responseType = ResponseType.FAIL,
                                message = message,
                                code = HttpStatus.BAD_REQUEST.value()
                            )
                        )
                    )
                return exchange.response.writeWith(Mono.just(buffer))
            }
        }
    }
}