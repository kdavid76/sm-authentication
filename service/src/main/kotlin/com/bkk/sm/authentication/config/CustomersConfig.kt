package com.bkk.sm.authentication.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "com.bkk.sm.customer-service")
data class CustomersConfig(
    val baseUri: String,
    val usersPath: String,
    val companiesPath: String,
    var apiVersion: String = "V1",
    var connectionTimeout: Int = 10000,
    var readTimeOut: Int = 100,
    var writeTimeOut: Int = 100,
    var useTimeouts: Boolean = true
)
