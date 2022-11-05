package com.bkk.sm.mongo.authentication.dto

data class AuthRequest(
        val username: String,
        val password: String
)