package com.bkk.sm.authentication.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebConfig(
    val customersConfig: CustomersConfig
) {

    @Qualifier("customerWebclient")
    @Bean
    fun customerWebClient(): WebClient {
        return if (customersConfig.useTimeouts) {
            val httpClient: HttpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, customersConfig.connectionTimeout)
                .doOnConnected {
                    it.addHandlerLast(ReadTimeoutHandler(customersConfig.readTimeOut))
                    it.addHandlerLast(WriteTimeoutHandler(customersConfig.writeTimeOut))
                }

            val connector = ReactorClientHttpConnector(httpClient)

            WebClient
                .builder()
                .baseUrl(customersConfig.baseUri)
                .clientConnector(connector)
                .defaultHeader("API_VERSION", customersConfig.apiVersion)
                .build()
        } else {
            WebClient
                .builder()
                .baseUrl(customersConfig.baseUri)
                .defaultHeader("API_VERSION", customersConfig.apiVersion)
                .build()
        }
    }
}
