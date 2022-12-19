package com.bkk.sm.authentication

import com.bkk.sm.authentication.config.CustomersConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = [
    "com.bkk.sm.mongo", "com.bkk.sm.mongo", "com.bkk.sm.authentication",
    "com.bkk.sm.jwt", "com.bkk.sm.common"
])
@EnableConfigurationProperties(CustomersConfig::class)
class AuthenticationServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthenticationServiceApplication>(*args)
}
