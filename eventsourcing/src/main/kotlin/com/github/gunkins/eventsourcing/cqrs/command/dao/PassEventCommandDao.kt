package com.github.gunkins.eventsourcing.cqrs.command.dao

import com.github.gunkins.eventsourcing.entity.PassEventType
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneOffset

@Repository
class PassEventCommandDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun insert(userId: Long, timestamp: Instant, eventType: PassEventType) {
        jdbcTemplate.update(
            """
                insert into pass_events(user_id, timestamp, event_type)
                values (:userId, :timestamp, :eventType::pass_event_type)
            """.trimIndent(),
            mapOf(
                "userId" to userId,
                "timestamp" to timestamp.atZone(ZoneOffset.UTC).toLocalDateTime(),
                "eventType" to eventType.name
            )
        )
    }
}