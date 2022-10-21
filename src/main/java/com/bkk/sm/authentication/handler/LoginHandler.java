package com.bkk.sm.authentication.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface LoginHandler {
    Mono<ServerResponse> login(final ServerRequest request);
}
