package com.github.gunkins.eventsourcing.cqrs.query.dao

import com.github.gunkins.eventsourcing.entity.PassEvent
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneOffset

@Repository
class PassEventQueryDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun getAllPassEventSortedByDate(from: Instant): List<PassEvent> {
        return jdbcTemplate.query(
            """
                select user_id, timestamp, event_type
                from pass_events
                order by timestamp
            """.trimIndent(),
            mapOf("from" to from.atZone(ZoneOffset.UTC).toLocalDateTime()),
            PASS_EVENT_MAPPER
        )
    }

    companion object {
        private val PASS_EVENT_MAPPER = RowMapper { rs, _ ->
            PassEvent(
                rs.getLong("user_id"),
                rs.getTimestamp("timestamp").toLocalDateTime().toInstant(ZoneOffset.UTC),
                enumValueOf(rs.getString("event_type"))
            )
        }
    }
}