package com.bkk.sm.authentication.handler

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface AuthenticationHandler {
    suspend fun login(request: ServerRequest): ServerResponse
    suspend fun registerUser(request: ServerRequest): ServerResponse
    suspend fun registerCompany(request: ServerRequest): ServerResponse
}
