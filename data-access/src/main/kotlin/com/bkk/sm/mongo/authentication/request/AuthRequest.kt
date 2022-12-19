package com.bkk.sm.mongo.authentication.request

data class AuthRequest(
        val username: String,
        val password: String
)