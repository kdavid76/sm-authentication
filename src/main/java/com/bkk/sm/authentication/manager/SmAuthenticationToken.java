package com.bkk.sm.authentication.manager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SmAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public SmAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public SmAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
