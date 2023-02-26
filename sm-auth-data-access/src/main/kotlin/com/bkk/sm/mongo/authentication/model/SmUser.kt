package com.bkk.sm.mongo.authentication.model

import com.bkk.sm.mongo.customers.model.user.UserProfile
import org.springframework.security.core.userdetails.User

class SmUser(userBase: UserProfile) :
    User(
        userBase.username,
        userBase.password,
        userBase.enabled,
        userBase.isAccountNonExpired(),
        userBase.isPasswordNonExpired(),
        !userBase.accountLocked,
        userBase.getGrantedAuthorities(),
    ) {

    val user: UserProfile = userBase

    fun isValid(): Boolean = isAccountNonExpired && isAccountNonLocked && isCredentialsNonExpired
}
