package com.bkk.sm.authentication.service

import com.bkk.sm.authentication.config.CustomersConfig
import com.bkk.sm.common.customer.resources.CompanyAndUserResource
import com.bkk.sm.common.customer.resources.CompanyResource
import com.bkk.sm.common.customer.resources.UserResource
import com.bkk.sm.common.customer.validators.CompanyResourceValidator
import com.bkk.sm.common.customer.validators.UserResourceValidator
import com.bkk.sm.common.errors.responses.FormErrorResource
import com.bkk.sm.jwt.JwtUtil
import com.bkk.sm.mongo.authentication.response.AuthResponse
import com.bkk.sm.mongo.authentication.model.SmUser
import com.bkk.sm.mongo.authentication.userdetails.UserDetailsService
import com.bkk.sm.mongo.customers.model.user.UserProfile
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class AuthenticationService (
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val userResourceValidator: UserResourceValidator,
    private val companyResourceValidator: CompanyResourceValidator,
    @Qualifier("customerWebclient") private val client: WebClient,
    private val customersConfig: CustomersConfig
){
    val log = KotlinLogging.logger {}

    suspend fun login(username: String, password: String) : ServerResponse {
        val user = userDetailsService.findByUsername(username).awaitSingleOrNull()

        user?.let {
            return doLogin(password, it as SmUser)
        }

        log.warn { "User with username=${username} can't be found" }
        return ServerResponse.notFound().buildAndAwait()
    }

    suspend fun registerUser(userProfile: UserResource): ServerResponse {
        val errors = validateUser(userProfile)

        if (errors.hasErrors()) {
            log.error { "Cannot register user, there are errors ${errors.allErrors.joinToString { "${it.code}" }}" }
            return ServerResponse.badRequest().bodyValueAndAwait(FormErrorResource.Builder()
                .objectName(UserResource::class.java.name)
                .addFieldErrors(errors)
                .build())
        }

        val resource = client.post()
            .uri(customersConfig.usersPath)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(userProfile))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { Mono.error { ResponseStatusException(it.statusCode())}}
            .onStatus(HttpStatus::is5xxServerError) { Mono.error { ResponseStatusException(it.statusCode())}}
            .bodyToMono(UserResource::class.java)
            .awaitSingleOrNull()

        resource?.let {
            log.info { "User=${it} successfully registered" }
            return ServerResponse.ok().body(BodyInserters.fromValue(it)).awaitSingle()
        }.run {
            log.error { "Something went wrong during registering user=${userProfile}" }
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
        }
    }

    suspend fun registerCompany(company: CompanyResource, user: UserResource?): ServerResponse {
        val companyErrors = validateCompany(company)
        val userErrors = user?.let {
            validateUser(user)
        } ?:  BeanPropertyBindingResult(user, UserResource::class.java.name)

        if (companyErrors.hasErrors() || userErrors.hasErrors()) {
            return ServerResponse.badRequest().bodyValueAndAwait(FormErrorResource.Builder()
                .objectName(CompanyAndUserResource::class.java.name)
                .addFieldErrors(companyErrors)
                .addFieldErrors(userErrors)
                .build())
        }

        val resource = client.post()
            .uri(customersConfig.companiesPath)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(CompanyAndUserResource(companyResource = company, userResource = user)))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError) { Mono.error { ResponseStatusException(it.statusCode())}}
            .onStatus(HttpStatus::is5xxServerError) { Mono.error { ResponseStatusException(it.statusCode())}}
            .bodyToMono(CompanyAndUserResource::class.java)
            .awaitSingleOrNull()

        resource?.let {
            log.info { "Company=${it.companyResource} with admin=${it.userResource ?: "N/A"} successfully registered" }
            return ServerResponse.ok().body(BodyInserters.fromValue(it)).awaitSingle()
        }.run {
            log.error { "Something went wrong during registering company=${company} with admin=${user ?: "N/A"}" }
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
        }
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

    private suspend fun validateUser(userResource: UserResource): Errors {
        val errors: Errors = BeanPropertyBindingResult(userResource, UserResource::class.java.name)
        userResourceValidator.validate(userResource, errors)
        return errors
    }

    private suspend fun validateCompany(companyResource: CompanyResource): Errors {
        val errors: Errors = BeanPropertyBindingResult(companyResource, CompanyResource::class.java.name)
        companyResourceValidator.validate(companyResource,errors)
        return errors
    }
}