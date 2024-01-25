package com.bkk.sm.mongo.authentication.response

import java.time.Instant

class AuthResponse(
    val token: String,
    var loginTime: Instant,
    var expirationTimeInSeconds: Long,
)
