package com.github.gunkins.eventsourcing.cqrs.query.dao

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Repository
class MembershipEventQueryDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val clock: Clock
) {
    fun getCurrentMembershipEnd(userId: Long): Instant? {
        val resultList = jdbcTemplate.queryForList(
            """
                select max(valid_to)
                from membership_events
                where user_id = :userId and valid_to > :validTo
            """.trimIndent(),
            mapOf("userId" to userId, "validTo" to LocalDate.now(clock)),
            Timestamp::class.java
        )
        return resultList.singleOrNull()?.toLocalDateTime()?.toInstant(ZoneOffset.UTC)
    }
}