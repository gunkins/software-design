package com.github.gunkins.reactive.dao

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.model.User

@Repository
class UserDao(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun insert(user: User): Mono<User> {
        return mongoTemplate.insert(user)
    }

    fun findUser(userId: String): Mono<User> {
        return mongoTemplate.findById(userId, User::class.java)
    }
}