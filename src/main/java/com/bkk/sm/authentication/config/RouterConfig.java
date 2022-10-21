package com.bkk.sm.authentication.config;

import com.bkk.sm.authentication.handler.LoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> loginRoutes(LoginHandler loginHandler) {
        return RouterFunctions
                .route(POST("/login"), loginHandler::login);
    }

    @Bean
    public RouterFunction<ServerResponse> somethingRoutes(LoginHandler loginHandler) {
        return RouterFunctions
                .route(POST("/something"), loginHandler::login);
    }
}
