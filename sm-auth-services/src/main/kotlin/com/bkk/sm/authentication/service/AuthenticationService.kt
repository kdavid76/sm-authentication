package com.bkk.sm.authentication.service

import com.bkk.sm.common.customer.resources.CompanyResource
import com.bkk.sm.common.customer.resources.UserResource
import org.springframework.web.reactive.function.server.ServerResponse

interface AuthenticationService {
    suspend fun login(username: String, password: String): ServerResponse
    suspend fun registerUser(userProfile: UserResource): ServerResponse
    suspend fun registerCompany(company: CompanyResource, user: UserResource?): ServerResponse
}
