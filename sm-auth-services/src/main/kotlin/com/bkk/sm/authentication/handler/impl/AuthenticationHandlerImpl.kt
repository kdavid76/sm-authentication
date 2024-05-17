package com.bkk.sm.authentication.handler.impl

import com.bkk.sm.authentication.handler.AuthenticationHandler
import com.bkk.sm.authentication.service.AuthenticationService
import com.bkk.sm.mongo.authentication.request.AuthRequest
import com.bkk.sm.mongo.authentication.request.RegCompanyRequest
import com.bkk.sm.mongo.authentication.request.RegUserRequest
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class AuthenticationHandlerImpl(
    val authService: AuthenticationService,
) : AuthenticationHandler {
    val log = KotlinLogging.logger {}

    override suspend fun login(request: ServerRequest): ServerResponse {
        val authRequest = request.awaitBodyOrNull<AuthRequest>()

        return if (StringUtils.isAnyBlank(authRequest?.password, authRequest?.username)) {
            log.error { "Invalid authentication request=$authRequest" }
            ServerResponse.badRequest().buildAndAwait()
        } else {
            authService.login(authRequest!!.username, authRequest.password)
        }
    }

    override suspend fun registerUser(request: ServerRequest): ServerResponse {
        val regUser = request.awaitBodyOrNull<RegUserRequest>()

        regUser?.let {
            it.user.let {
                val response = authService.registerUser(it)

                return response
            }
        }
        return ServerResponse.badRequest().buildAndAwait()
    }

    override suspend fun registerCompany(request: ServerRequest): ServerResponse {
        val regCompany = request.awaitBodyOrNull<RegCompanyRequest>()

        regCompany?.let {
            return authService.registerCompany(it.company, it.user)
        }
        return ServerResponse.badRequest().buildAndAwait()
    }
}
