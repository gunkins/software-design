package com.github.gunkins.eventsourcing.integration

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gunkins.eventsourcing.cqrs.command.dao.PassEventCommandDao
import com.github.gunkins.eventsourcing.cqrs.query.service.ReportService
import com.github.gunkins.eventsourcing.entity.PassEventType
import com.github.gunkins.eventsourcing.entity.UserReport
import com.github.gunkins.eventsourcing.instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneOffset

class ReportIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var passEventCommandDao: PassEventCommandDao

    @Autowired
    private lateinit var reportService: ReportService

    private val jsonMapper = jsonMapper {
        addModule(kotlinModule())
        addModule(JavaTimeModule())
    }

    @Test
    fun `User entered and left`() {
        val user = 10L
        val enter = instant("2022-03-01 12:00:00")
        val date = LocalDate.ofInstant(enter, ZoneOffset.UTC)
        val userTime = Duration.ofMinutes(30)

        passEventCommandDao.insert(user, enter, PassEventType.ENTRANCE)
        passEventCommandDao.insert(user, enter + userTime, PassEventType.EXIT)

        reportService.scheduledUpdateData()

        mockMvc.perform(get("/report/$user"))
            .andExpect(status().isOk)
            .andDo {
                val expected = UserReport(user, 1, userTime)
                val report = jsonMapper.readValue<UserReport>(it.response.contentAsString)
                assertThat(report).isEqualTo(expected)
            }

        mockMvc.perform(
            get("/report/visits")
                .param("from", LocalDate.EPOCH.toString())
                .param("to", LocalDate.ofInstant(enter + userTime, ZoneOffset.UTC).toString())
        ).andExpect(status().isOk)
            .andDo {
                val expected = mapOf(date to 1)
                val visits = jsonMapper.readValue<Map<LocalDate, Int>>(it.response.contentAsString)
                assertThat(visits).isEqualTo(expected)
            }
    }
}