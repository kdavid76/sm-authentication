package com.bkk.sm.authentication.handler

import com.bkk.sm.jwt.util.JwtUtil
import com.bkk.sm.mongo.authentication.dto.AuthRequest
import com.bkk.sm.mongo.authentication.dto.AuthResponse
import com.bkk.sm.mongo.authentication.model.SmUser
import com.bkk.sm.mongo.authentication.userdetails.UserDetailsService
import com.bkk.sm.mongo.customers.model.user.UserProfile
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.Instant

@Component
class AuthenticationHandler(
        private val userDetailsService: UserDetailsService,
        private val jwtUtil: JwtUtil,
        private val passwordEncoder: PasswordEncoder
) {

    val log = KotlinLogging.logger {}

    suspend fun login(request: ServerRequest): ServerResponse {
        val authRequest = request.awaitBodyOrNull<AuthRequest>()

        authRequest?.let { it ->
            val user = userDetailsService.findByUsername(it.username).awaitSingleOrNull()

            user?.let {
                return doLogin(authRequest.password, it as SmUser)
            }
        }

        log.warn { "User with username=${authRequest?.username ?: "N/A"} can't be found" }
        return ServerResponse.notFound().buildAndAwait()
    }

    suspend fun registerUser(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun registerCompany(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().buildAndAwait()
    }

    private suspend fun doLogin(password: String, smUser: SmUser): ServerResponse {

        if (!smUser.isValid() || !passwordEncoder.matches(password, smUser.user.password)) {
            log.error {
                "Access forbidden for user=${smUser.username}. isAccountNonExpired=${smUser.isAccountNonExpired}, " +
                        "isAccountNonLocked=${smUser.isAccountNonLocked}, isCredentialsNonExpired=${smUser.isCredentialsNonExpired}"
            }
            return ServerResponse.status(HttpStatus.FORBIDDEN).build().awaitSingle()
        }

        val token = jwtUtil.generateToken(calculateClaimsMap(smUser.user), smUser.username)

        log.info { "Login successful for user=${smUser.username}, JWT generated." }
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(AuthResponse(token!!, Instant.now()))
    }

    private fun calculateClaimsMap(userProfile: UserProfile): MutableMap<String, Any?> =
            mutableMapOf(
                    Pair("roles", userProfile.roles)
            )
}