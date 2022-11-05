package com.bkk.sm.jwt.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.util.*
import javax.annotation.PostConstruct


@Component
class JwtUtil {

    @Value("\${com.bkk.sm.jwt.secret}")
    val secret: String = ""

    @Value("\${com.bkk.sm.jwt.expiration}")
    val expirationTimeIsSeconds: Long = 10 * 60 // 10 minutes

    @Value("\${com.bkk.sm.jwt.issuer}")
    val issuer: String = ""

    private var key: Key? = null

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secret.toByteArray());
    }

    fun generateToken(claims: Map<String, Any?>, username: String): String? {
        val createdDate: Date = Date.from(Instant.now())
        val expirationDate = Date(createdDate.time + expirationTimeIsSeconds * 1000)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .setIssuer(issuer)
                .signWith(key)
                .compact()
    }
}