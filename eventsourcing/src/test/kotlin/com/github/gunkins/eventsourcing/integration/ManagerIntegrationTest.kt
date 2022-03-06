package com.github.gunkins.eventsourcing.integration

import com.github.gunkins.eventsourcing.cqrs.command.service.MembershipEventCommandService
import com.github.gunkins.eventsourcing.cqrs.query.dao.MembershipEventQueryDao
import com.github.gunkins.eventsourcing.instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit

class ManagerIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var membershipEventCommandService: MembershipEventCommandService

    @Autowired
    private lateinit var membershipEventQueryDao: MembershipEventQueryDao

    private val now = instant("2022-03-01 12:00:00")

    @BeforeEach
    fun beforeEach() {
        clock.setFixed(now)
    }

    @Test
    fun `Successfully register user`() {
        val user = 2L
        val days = 15L

        mockMvc.perform(post("/manager/register?userId=$user&days=$days"))
            .andExpect(status().isOk)

        val expected = now.plus(days, ChronoUnit.DAYS)
        val actual = membershipEventQueryDao.getCurrentMembershipEnd(user)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Register already registered user - bad request`() {
        val user = 2L
        val days = 15L
        membershipEventCommandService.registerMembership(user, days)

        mockMvc.perform(post("/manager/register?userId=$user&days=100"))
            .andExpect(status().isBadRequest)

        val expected = now.plus(days, ChronoUnit.DAYS)
        val actual = membershipEventQueryDao.getCurrentMembershipEnd(user)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Successfully renew user membership`() {
        val user = 2L
        val initialDays = 15L
        val addDays = 5L
        membershipEventCommandService.registerMembership(user, initialDays)

        val renewUntil = now.plus(initialDays + addDays, ChronoUnit.DAYS)
        mockMvc.perform(post("/manager/renew?userId=$user&renewUntil=$renewUntil"))
            .andExpect(status().isOk)

        assertThat(membershipEventQueryDao.getCurrentMembershipEnd(user))
            .isEqualTo(renewUntil)
    }

    @Test
    fun `Renew of user who does not have membership`() {
        val user = 2L
        val renewUntil = now.plus(10, ChronoUnit.DAYS)
        mockMvc.perform(post("/manager/renew?userId=$user&renewUntil=$renewUntil"))
            .andExpect(status().isBadRequest)

        assertThat(membershipEventQueryDao.getCurrentMembershipEnd(user))
            .isNull()
    }

    @Test
    fun `Renew membership to date in past`() {
        val user = 2L
        val days = 15L
        membershipEventCommandService.registerMembership(user, days)

        val renewUntil = now.minus(5, ChronoUnit.DAYS)
        mockMvc.perform(post("/manager/renew?userId=$user&renewUntil=$renewUntil"))
            .andExpect(status().isBadRequest)

        assertThat(membershipEventQueryDao.getCurrentMembershipEnd(user))
            .isEqualTo(now.plus(days, ChronoUnit.DAYS))
    }
}