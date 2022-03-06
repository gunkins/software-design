package com.github.gunkins.eventsourcing.controller

import com.github.gunkins.eventsourcing.cqrs.command.service.PassEventCommandService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/turnstile")
class TurnstileController(
    private val passEventCommandService: PassEventCommandService
) {
    @PostMapping("/enter")
    fun enter(@RequestParam userId: Long) {
        passEventCommandService.userEntrance(userId)
    }

    @PostMapping("/exit")
    fun exit(@RequestParam userId: Long) {
        passEventCommandService.userExit(userId)
    }
}