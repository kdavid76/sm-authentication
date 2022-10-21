package com.bkk.sm.authentication.manager;

import com.bkk.sm.authentication.jwt.JWTUtil;
import com.bkk.sm.mongo.authentication.model.CompanyRole;
import com.bkk.sm.mongo.authentication.model.User;
import com.bkk.sm.mongo.authentication.service.ReactiveUserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class SmAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtil jwtUtil;
    private final ReactiveUserService userService;

    private final UserDetailsChecker preAuthenticationChecks = this::defaultPreAuthenticationChecks;
    private final UserDetailsChecker postAuthenticationChecks = this::defaultPostAuthenticationChecks;

    @Autowired
    public SmAuthenticationManager(JWTUtil jwtUtil, ReactiveUserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username = jwtUtil.getUsernameFromToken(authToken);

        return userService.findByUsername(username)
                .onErrorResume(Objects::nonNull, Mono::error)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(
                        String.format("Username=%s is not found.", username)
                ))))
                .doOnNext(this.preAuthenticationChecks::check)
                .filter(userDetails -> jwtUtil.validateToken(authToken))
                .doOnNext(this.postAuthenticationChecks::check)
                .cast(User.class)
                .map(user -> {
                    Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
                    List<CompanyRole> rolesMap = claims.get("role", List.class);
                    return new SmAuthenticationToken(rolesMap, username, null);
                });
    }

    private void defaultPreAuthenticationChecks(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException(String.format("User account=%s is locked", user.getUsername()));
        }
        if (!user.isEnabled()) {
            throw new DisabledException(String.format("User=%s is disabled", user.getUsername()));
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(String.format("User account=%s has expired", user.getUsername()));
        }
    }

    private void defaultPostAuthenticationChecks(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(String.format("User=%s credentials have expired", user.getUsername()));
        }
    }
}
