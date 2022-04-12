package com.bkk.sm.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString(exclude = {"password", "authorities"})
@EqualsAndHashCode(exclude = {"password", "authorities"})
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Serial
    private static final long serialVersionUID = 2405172041950251808L;

    @Id
    private String id;

    @Indexed
    @NonNull
    private String username;

    @NonNull
    @JsonIgnore
    private String password;

    @NonNull
    private String firstName;

    @NonNull
    @Indexed
    private String lastName;

    @Indexed
    @NonNull
    private String email;

    private List<CompanyRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @JsonIgnore
    private Integer failedLoginAttempts = 0;

    @Version
    int version;

    private Date registrationTime;
    private Date lastModificationTime;
    private Date passwordExpiryTime;
    private Date activatedTime;
    private boolean accountExpired = false;
    private boolean accountLocked = false;
    private boolean enabled = false;
    private String middleName;
    private String activationCode;

    @Transient
    @JsonIgnore
    private List<SimpleGrantedAuthority> authorities;

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !this.accountExpired;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return Instant.now().isAfter(passwordExpiryTime.toInstant());
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setPassword(String password) {
        this.password = password;
    }

    public void activate() {
        this.activationCode = "";
        this.lastModificationTime = Date.from(Instant.now());
        this.activatedTime = lastModificationTime;
        this.enabled = true;
        this.accountLocked = false;
        this.accountExpired = false;
    }

    public void updateWith(User newVersion) {
        boolean changed = false;
        if (newVersion != null &&
                StringUtils.isNotBlank(newVersion.getFirstName()) &&
                !newVersion.getFirstName().equalsIgnoreCase(firstName)) {

            firstName = newVersion.getFirstName();
            changed = true;
        }

        if (newVersion != null &&
                StringUtils.isNotBlank(newVersion.getLastName()) &&
                    !newVersion.getLastName().equalsIgnoreCase(this.lastName)) {
            lastName = newVersion.getLastName();
            changed = true;
        }

        if (newVersion != null && StringUtils.isNotBlank(newVersion.getMiddleName()) && !newVersion.getMiddleName().equalsIgnoreCase(this.middleName)) {
            this.middleName = newVersion.getMiddleName();
            changed = true;
        }

        if (newVersion != null && StringUtils.isNotBlank(newVersion.getEmail()) &&
                !newVersion.getEmail().equalsIgnoreCase(this.email)) {
            email = newVersion.getEmail();
            changed = true;
        }

        if (changed) {
            lastModificationTime = Date.from(Instant.now());
        }

    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void addCompanyRole(CompanyRole companyRole) {
        if(roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(companyRole);
    }

    public void addCompanyRole(String companyCode, Role role) {

        addCompanyRole(CompanyRole.builder()
                .companyCode(companyCode)
                .role(role)
                .build()
        );
    }


    public List<String> getCompanies() {
        return roles==null ? List.of() :
                roles.parallelStream().map(CompanyRole::getCompanyCode)
                        .distinct().toList();
    }

    @JsonIgnore
    public boolean isSuperAdmin() {
        return getAuthorities().stream().anyMatch(sga ->
                sga.getAuthority().equals(Role.ROLE_SUPERADMIN.name()));
    }

    @JsonIgnore
    public boolean isAdmin() {
        return getAuthorities().stream().anyMatch(sga ->
                sga.getAuthority().equals(Role.ROLE_SUPERADMIN.name()) ||
                        sga.getAuthority().equals(Role.ROLE_ADMIN.name()));
    }

    public void resetFailedLoginAttempts() {
        failedLoginAttempts = 0;
    }

    public void increaseFailedLoginAttempts(int limit) {
        failedLoginAttempts++;

        if(failedLoginAttempts >= limit) {
            accountLocked = true;
        }
    }
}
