package com.bkk.sm.authentication.manager;

import com.bkk.sm.mongo.authentication.model.CompanyRole;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class SmAuthenticationToken extends UsernamePasswordAuthenticationToken {


    private final List<CompanyRole> companyRoles;

    public SmAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
        companyRoles = null;
    }

    public SmAuthenticationToken(List<CompanyRole> companyRoles, Object principal, Object credentials) {
        super(principal, credentials, companyRoles.stream()
                .map(CompanyRole::getRole)
                .distinct()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList()
        );
        this.companyRoles = companyRoles;
    }
}
