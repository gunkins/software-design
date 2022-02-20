package com.github.gunkins.reactive.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.model.Currency
import com.github.gunkins.reactive.dao.model.User
import com.github.gunkins.reactive.service.UserService

@RestController
class UserController(
    private val userService: UserService
) {
    @PostMapping("/user")
    fun register(
        @RequestParam name: String,
        @RequestParam currency: Currency
    ): Mono<User> {
        return userService.add(name, currency)
    }
}