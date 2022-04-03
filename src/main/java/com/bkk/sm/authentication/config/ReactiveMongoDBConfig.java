package com.bkk.sm.authentication.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Slf4j
@Configuration
@EnableReactiveMongoRepositories
@EnableEncryptableProperties
public class ReactiveMongoDBConfig extends AbstractReactiveMongoConfiguration {

    private static final String PASSWORD_TAG = "[PASSWORD]";

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    /* c9MEobpSpR9FtCWQ */
    @Value("${spring.data.mongodb.password}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString(getConnectionString());
        builder
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build());
    }
    private String getConnectionString() {
        return uri.replace(PASSWORD_TAG, password);
    }
}
