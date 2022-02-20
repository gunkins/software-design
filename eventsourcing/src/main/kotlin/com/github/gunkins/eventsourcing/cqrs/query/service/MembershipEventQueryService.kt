package com.github.gunkins.eventsourcing.cqrs.query.service

import com.github.gunkins.eventsourcing.cqrs.query.dao.MembershipEventQueryDao
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MembershipEventQueryService(
    private val membershipEventQueryDao: MembershipEventQueryDao
) {
    fun getCurrentMembershipEnd(userId: Long): Instant? {
        return membershipEventQueryDao.getCurrentMembershipEnd(userId)
    }
}