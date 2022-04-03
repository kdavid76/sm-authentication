package com.bkk.sm.authentication.services.mongo;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserDetailsService extends ReactiveUserDetailsService {
    Mono<UserDetails> findByUsername(String username);
}
