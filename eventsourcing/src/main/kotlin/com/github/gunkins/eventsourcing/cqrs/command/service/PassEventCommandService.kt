package com.github.gunkins.eventsourcing.cqrs.command.service

import com.github.gunkins.eventsourcing.cqrs.command.dao.PassEventCommandDao
import com.github.gunkins.eventsourcing.entity.PassEventType
import com.github.gunkins.eventsourcing.exception.BadRequestException
import com.github.gunkins.eventsourcing.cqrs.query.dao.MembershipEventQueryDao
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class PassEventCommandService(
    private val passEventDao: PassEventCommandDao,
    private val membershipEventQueryDao: MembershipEventQueryDao,
    private val clock: Clock
) {
    fun userEntrance(userId: Long) {
        if (membershipEventQueryDao.getCurrentMembershipEnd(userId) == null) {
            throw BadRequestException("User $userId doesn't have valid membership")
        }

        passEventDao.insert(userId, Instant.now(clock), PassEventType.ENTRANCE)
    }

    fun userExit(userId: Long) {
        passEventDao.insert(userId, Instant.now(clock), PassEventType.EXIT)
    }
}