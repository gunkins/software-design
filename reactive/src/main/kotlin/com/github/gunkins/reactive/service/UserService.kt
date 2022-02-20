package com.github.gunkins.reactive.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.UserDao
import com.github.gunkins.reactive.dao.model.Currency
import com.github.gunkins.reactive.dao.model.User
import com.github.gunkins.reactive.exception.UserNotFoundException

@Service
class UserService(
    private val userDao: UserDao
) {
    fun getUser(userId: String): Mono<User> {
        return userDao.findUser(userId)
            .switchIfEmpty(Mono.error(UserNotFoundException("User $userId not found")))
    }

    fun add(name: String, currency: Currency): Mono<User> {
        return userDao.insert(User(name = name, currency = currency))
    }
}