package com.bkk.sm.authentication.handler.impl;

import com.bkk.sm.authentication.handler.LoginHandler;
import com.bkk.sm.authentication.jwt.JWTUtil;
import com.bkk.sm.authentication.model.AuthRequest;
import com.bkk.sm.authentication.model.AuthResponse;
import com.bkk.sm.authentication.services.mongo.UserDetailsService;
import com.bkk.sm.mongo.authentication.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class LoginHandlerImp implements LoginHandler {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public LoginHandlerImp(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                           JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<ServerResponse> login(final ServerRequest request) {

        Mono<AuthRequest> requestMono = request.bodyToMono(AuthRequest.class);

        return requestMono.flatMap(authRequest -> {
            if(StringUtils.isAnyBlank(authRequest.getPassword(), authRequest.getUsername())) {
                log.info("Empty username={} or password", authRequest.getUsername());
                return ServerResponse.badRequest().build();
            }
            return userDetailsService.findByUsername(authRequest.getUsername())
                    .filter(userDetails -> passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword()))
                    .flatMap(userDetails -> ServerResponse.ok().body(BodyInserters.fromValue(
                            new AuthResponse(
                                    jwtUtil.generateToken((User) userDetails),
                                    Date.from(Instant.now())
                            ))))
                    .switchIfEmpty(Mono.defer(() -> {
                        log.info("The username={} or password is not found in datasource.", authRequest.getUsername());
                        return ServerResponse.notFound().build();
                    }));
        })
        .switchIfEmpty(Mono.defer(() -> {
            log.info("Authentication request can't be found.");
            return ServerResponse.status(HttpStatus.BAD_REQUEST).build();
        }));
    }
}
