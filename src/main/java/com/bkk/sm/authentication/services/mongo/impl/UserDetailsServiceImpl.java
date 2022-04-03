package com.bkk.sm.authentication.services.mongo.impl;

import com.bkk.sm.authentication.services.mongo.UserDetailsService;
import com.bkk.sm.authentication.model.Role;
import com.bkk.sm.authentication.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Map<String, User> userMap;

    public UserDetailsServiceImpl() {
        userMap = new HashMap<>();

        User u1 = new User("admin", "$2a$10$Ryz84DvVZkr2ewqMV/B7BeDr4cf4rpOYP2I1jwXCRVUb4ayZcuIXy", true, List.of(Role.ROLE_USER, Role.ROLE_ADMIN));
        User u2 = new User("user", "$2a$10$6pVt01bVgbBXl/LblaEaxObtknqnLsfi7bKAcnCI/ZOtIswNnEAC6", true, List.of(Role.ROLE_ADMIN));

        userMap.put(u1.getUsername(), u1);
        userMap.put(u2.getUsername(), u2);
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(userMap.get(username));
    }
}
