package com.bkk.sm.authentication.config

import com.bkk.sm.authentication.handler.AuthenticationHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfig {
    val log = KotlinLogging.logger {}

    @Bean
    fun userRoutes(authHandler: AuthenticationHandler) = coRouter {
        before {
            log.info { "Processing authentication request from ${it.remoteAddress().orElse(null)} with headers=${it.headers()}" }
            it
        }

        "/login".nest {
            headers {
                it.header("API_VERSION")[0].equals("V1")
            }.nest {
                contentType(MediaType.APPLICATION_JSON).nest {
                    POST("", authHandler::login)
                }
            }
        }

        "/register".nest {
            headers {
                it.header("API_VERSION")[0].equals("V1")
            }.nest {
                contentType(MediaType.APPLICATION_JSON).nest {
                    POST("/user", authHandler::registerUser)
                    POST("/company", authHandler::registerCompany)
                }
            }
        }
    }
}
