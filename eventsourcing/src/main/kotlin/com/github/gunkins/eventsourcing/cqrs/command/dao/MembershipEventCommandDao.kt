package com.github.gunkins.eventsourcing.cqrs.command.dao

import com.github.gunkins.eventsourcing.entity.MembershipEventType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneOffset

@Repository
class MembershipEventCommandDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun insert(userId: Long, timestamp: Instant, validTo: Instant, eventType: MembershipEventType) {
        jdbcTemplate.update(
            """
                insert into membership_events(user_id, timestamp, valid_to, event_type)
                values (:userId, :timestamp, :validTo, :eventType::membership_event_type)
            """.trimIndent(),
            mapOf(
                "userId" to userId,
                "timestamp" to timestamp.atZone(ZoneOffset.UTC).toLocalDateTime(),
                "validTo" to validTo.atZone(ZoneOffset.UTC).toLocalDateTime(),
                "eventType" to eventType.name
            )
        )
    }
}