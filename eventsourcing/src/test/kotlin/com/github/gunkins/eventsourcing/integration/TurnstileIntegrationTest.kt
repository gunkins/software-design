package com.github.gunkins.eventsourcing.integration

import com.github.gunkins.eventsourcing.cqrs.command.service.MembershipEventCommandService
import com.github.gunkins.eventsourcing.cqrs.query.dao.PassEventQueryDao
import com.github.gunkins.eventsourcing.entity.PassEvent
import com.github.gunkins.eventsourcing.entity.PassEventType
import com.github.gunkins.eventsourcing.instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit

class TurnstileIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var membershipCommandService: MembershipEventCommandService
    @Autowired
    private lateinit var passEventQueryDao: PassEventQueryDao

    private val now = instant("2022-03-01 12:00:00")

    @BeforeEach
    fun beforeEach() {
        clock.setFixed(now)
    }

    @Test
    fun `Pass user with existent membership`() {
        val user = 1L
        membershipCommandService.registerMembership(user, 10)

        mockMvc.perform(post("/turnstile/enter?userId=$user"))
            .andExpect(status().isOk)

        val expected = PassEvent(user, now, PassEventType.ENTRANCE)
        val actual = passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH)

        assertThat(actual)
            .containsExactly(expected)
    }

    @Test
    fun `Pass user without membership produces bad request`() {
        mockMvc.perform(post("/turnstile/enter?userId=1"))
            .andExpect(status().isBadRequest)

        assertThat(passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun `Pass user with outdated membership produces bad request`() {
        val user = 1L
        membershipCommandService.registerMembership(user, 10)

        clock.setFixed(now.plus(15, ChronoUnit.DAYS))

        mockMvc.perform(post("/turnstile/enter?userId=$user"))
            .andExpect(status().isBadRequest)

        assertThat(passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH))
            .isEmpty()
    }

    @Test
    fun `User exit without membership successfully`() {
        mockMvc.perform(post("/turnstile/exit?userId=1"))
            .andExpect(status().isOk)

        val expected = PassEvent(1, now, PassEventType.EXIT)
        val actual = passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH)

        assertThat(actual)
            .containsExactly(expected)
    }
}