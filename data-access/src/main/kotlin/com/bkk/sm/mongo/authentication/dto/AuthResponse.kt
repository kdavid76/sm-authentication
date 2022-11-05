package com.bkk.sm.mongo.authentication.dto

import java.time.Instant

class AuthResponse(
        val token: String,
        var loginTime: Instant
)