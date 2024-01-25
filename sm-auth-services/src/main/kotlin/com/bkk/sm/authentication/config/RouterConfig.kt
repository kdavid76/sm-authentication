package com.bkk.sm.authentication.config

import com.bkk.sm.authentication.handler.impl.AuthenticationHandlerImpl
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfig {
    val log = KotlinLogging.logger {}

    @Bean
    fun userRoutes(authHandler: AuthenticationHandlerImpl) = coRouter {
        before {
            log.info { "Processing authentication request from ${it.remoteAddress().orElse(null)} to ${it.path()}" }
            it
        }

        headers {
            it.header("API_VERSION")[0] == "V1"
        }.nest {
            contentType(MediaType.APPLICATION_JSON).nest {
                POST("/login", authHandler::login)

                "/register".nest {
                    POST("/user", authHandler::registerUser)
                    POST("/company", authHandler::registerCompany)
                }
            }
        }

        after { serverRequest, serverResponse ->
            logResponse(serverRequest, serverResponse)
        }

    }

    private fun logResponse(serverRequest: ServerRequest, serverResponse: ServerResponse): ServerResponse {
        log.info { "Request processed to ${serverRequest.path()} with result ${serverResponse.statusCode()}" }
        return  serverResponse
    }
}
