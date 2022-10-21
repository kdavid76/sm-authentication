package com.bkk.sm.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = {
		"com.bkk.sm.authentication",
		"com.bkk.sm.mongo.authentication"
})
@EnableDiscoveryClient
@EnableReactiveMongoRepositories({"com.bkk.sm.mongo.authentication.repository"})
@EntityScan({"com.bkk.sm.mongo.authentication.model"})
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
