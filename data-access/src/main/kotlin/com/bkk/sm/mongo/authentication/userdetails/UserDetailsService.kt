package com.bkk.sm.mongo.authentication.userdetails

import com.bkk.sm.mongo.authentication.model.SmUser
import com.bkk.sm.mongo.customers.repositories.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(
    private val repository: UserRepository
) : ReactiveUserDetailsService {
    override fun findByUsername(username: String?): Mono<UserDetails> = mono {
        val user = repository.findByUsername(username!!)
        user?.let {
            return@mono SmUser(it)
        }
        return@mono null
    }
}
