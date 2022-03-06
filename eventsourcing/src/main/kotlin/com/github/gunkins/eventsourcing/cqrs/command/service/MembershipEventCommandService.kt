package com.github.gunkins.eventsourcing.cqrs.command.service

import com.github.gunkins.eventsourcing.entity.MembershipEventType
import com.github.gunkins.eventsourcing.cqrs.command.dao.MembershipEventCommandDao
import com.github.gunkins.eventsourcing.exception.BadRequestException
import com.github.gunkins.eventsourcing.cqrs.query.service.MembershipEventQueryService
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Service
class MembershipEventCommandService(
    private val commandDao: MembershipEventCommandDao,
    private val queryService: MembershipEventQueryService,
    private val clock: Clock
) {
    fun registerMembership(userId: Long, daysDuration: Long) {
        val now = Instant.now(clock)
        val currentMembershipEnd: Instant? = queryService.getCurrentMembershipEnd(userId)

        if (currentMembershipEnd != null) {
            throw BadRequestException("User $userId already has a membership until $currentMembershipEnd")
        }

        val validTo = now + Duration.ofDays(daysDuration)
        commandDao.insert(userId, now, validTo, MembershipEventType.REGISTRATION)
    }

    fun renewMembership(userId: Long, renewUntil: Instant) {
        val now = Instant.now(clock)
        val currentMembershipEnd: Instant = queryService.getCurrentMembershipEnd(userId)
            ?: throw BadRequestException("User $userId doesn't have a membership")

        if (currentMembershipEnd >= renewUntil) {
            throw BadRequestException("Existing membership ends later than $renewUntil")
        }

        commandDao.insert(userId, now, renewUntil, MembershipEventType.RENEW)
    }
}