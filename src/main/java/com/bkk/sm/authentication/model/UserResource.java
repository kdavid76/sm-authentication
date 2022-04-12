package com.bkk.sm.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResource {
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private List<CompanyRole> roles;
    private Integer failedLoginAttempts = 0;
    int version;
    private Date registrationTime;
    private Date lastModificationTime;
    private Date passwordExpiryTime;
    private Date activatedTime;
    private boolean accountExpired = false;
    private boolean accountLocked = false;
    private boolean enabled = false;
    private String activationCode;
}
