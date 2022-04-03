package com.bkk.sm.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);

		//BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		//System.out.println(encoder.encode("user"));
		/*
		ConnectionString connectionString = new ConnectionString("mongodb+srv://taoElszamolas:c9MEobpSpR9FtCWQ@taocluster.qa3sd.mongodb.net/users?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.serverApi(ServerApi.builder()
						.version(ServerApiVersion.V1)
						.build())
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("users");
		MongoCollection<Document> collection = database.getCollection("users");
		collection.insertOne(Document.parse("{name: 2345}"));
		System.out.println(String.format("Database name: %s", database.getName()));
		 */
	}
}
