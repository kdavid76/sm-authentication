package com.bkk.sm.authentication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = [
    "com.bkk.sm.mongo.customers", "com.bkk.sm.mongo.authentication", "com.bkk.sm.authentication",
    "com.bkk.sm.jwt"
])
class AuthenticationServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthenticationServiceApplication>(*args)
}
