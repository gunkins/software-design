package com.github.gunkins.eventsourcing.controller

import com.github.gunkins.eventsourcing.cqrs.command.service.MembershipEventCommandService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/manager")
class ManagerController(
    private val membershipEventCommandService: MembershipEventCommandService
) {
    @PostMapping("/register")
    fun register(@RequestParam userId: Long, @RequestParam days: Long) {
        membershipEventCommandService.registerMembership(userId, days)
    }

    @PostMapping("/renew")
    fun renew(@RequestParam userId: Long, @RequestParam renewUntil: Instant) {
        membershipEventCommandService.renewMembership(userId, renewUntil)
    }
}