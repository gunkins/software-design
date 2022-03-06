package com.github.gunkins.eventsourcing.unit

import com.github.gunkins.eventsourcing.cqrs.query.dao.PassEventQueryDao
import com.github.gunkins.eventsourcing.cqrs.query.service.ReportService
import com.github.gunkins.eventsourcing.entity.PassEvent
import com.github.gunkins.eventsourcing.entity.PassEventType
import com.github.gunkins.eventsourcing.entity.UserReport
import com.github.gunkins.eventsourcing.instant
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class ReportServiceUnitTest {
    @MockK
    private lateinit var passEventQueryDao: PassEventQueryDao

    private lateinit var reportService: ReportService

    @BeforeEach
    fun beforeEach() {
        reportService = ReportService(passEventQueryDao)
    }

    @Test
    fun `No events happened at all`() {
        every { passEventQueryDao.getAllPassEventSortedByDate(any()) } returns emptyList()

        reportService.scheduledUpdateData()

        val someUserReport = reportService.getUserReport(10)
        val visits = reportService.getVisits(LocalDate.MIN, LocalDate.MAX)

        assertThat(someUserReport).isEqualTo(UserReport(10, 0, Duration.ZERO))
        assertThat(visits).isEmpty()

        verify { passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH) }
        confirmVerified(passEventQueryDao)
    }

    @Test
    fun `User entered but did not exit`() {
        val user = 10L
        val instant = instant("2022-03-01 12:00:00")
        val date = LocalDate.ofInstant(instant, ZoneOffset.UTC)
        val event = PassEvent(user, instant, PassEventType.ENTRANCE)

        every { passEventQueryDao.getAllPassEventSortedByDate(any()) } returns listOf(event)

        reportService.scheduledUpdateData()

        val userReport = reportService.getUserReport(user)
        val visits = reportService.getVisits(LocalDate.MIN, LocalDate.MAX)

        assertThat(userReport).isEqualTo(UserReport(10, 0, Duration.ZERO))
        assertThat(visits).isEqualTo(mapOf(date to 1))
    }

    @Test
    fun `User left twice but did not enter`() {
        val user = 10L
        val instant = instant("2022-03-01 12:00:00")

        every {
            passEventQueryDao.getAllPassEventSortedByDate(any())
        } returns listOf(
            PassEvent(user, instant, PassEventType.EXIT),
            PassEvent(user, instant.plusSeconds(100), PassEventType.EXIT)
        )

        reportService.scheduledUpdateData()

        val userReport = reportService.getUserReport(user)
        val visits = reportService.getVisits(LocalDate.MIN, LocalDate.MAX)

        assertThat(userReport).isEqualTo(UserReport(user, 0, Duration.ZERO))
        assertThat(visits).isEmpty()
    }

    @Test
    fun `User entered and left`() {
        val user = 10L
        val enter = instant("2022-03-01 12:00:00")
        val date = LocalDate.ofInstant(enter, ZoneOffset.UTC)
        val userTime = Duration.ofMinutes(30)

        every {
            passEventQueryDao.getAllPassEventSortedByDate(any())
        } returns listOf(
            PassEvent(user, enter, PassEventType.ENTRANCE),
            PassEvent(user, enter + userTime, PassEventType.EXIT)
        )

        reportService.scheduledUpdateData()

        val userReport = reportService.getUserReport(user)
        val visits = reportService.getVisits(LocalDate.MIN, LocalDate.MAX)

        assertThat(userReport).isEqualTo(UserReport(user, 1, userTime))
        assertThat(visits).isEqualTo(mapOf(date to 1))
    }

    @Test
    fun `Multiple user entered and left`() {
        val firstCall = listOf(
            PassEvent(1, instant("2022-03-01 12:00:00"), PassEventType.ENTRANCE),
            PassEvent(2, instant("2022-03-01 12:30:00"), PassEventType.ENTRANCE),
            PassEvent(2, instant("2022-03-01 12:40:00"), PassEventType.EXIT),
            PassEvent(3, instant("2022-03-01 13:00:00"), PassEventType.ENTRANCE),
            PassEvent(1, instant("2022-03-01 13:10:00"), PassEventType.EXIT),
        )

        val secondCall = listOf(
            PassEvent(3, instant("2022-03-01 13:20:00"), PassEventType.EXIT),
            PassEvent(1, instant("2022-03-03 19:00:00"), PassEventType.ENTRANCE),
            PassEvent(1, instant("2022-03-03 19:20:00"), PassEventType.EXIT),
        )

        every {
            passEventQueryDao.getAllPassEventSortedByDate(any())
        } returnsMany listOf(firstCall, secondCall)

        reportService.scheduledUpdateData()
        reportService.scheduledUpdateData()

        assertThat(reportService.getUserReport(1))
            .isEqualTo(UserReport(1, 2, Duration.ofMinutes(90)))
        assertThat(reportService.getUserReport(2))
            .isEqualTo(UserReport(2, 1, Duration.ofMinutes(10)))
        assertThat(reportService.getUserReport(3))
            .isEqualTo(UserReport(3, 1, Duration.ofMinutes(20)))

        assertThat(reportService.getVisits(LocalDate.MIN, LocalDate.MAX))
            .isEqualTo(
                mapOf(
                    LocalDate.of(2022, 3, 1) to 3,
                    LocalDate.of(2022, 3, 3) to 1
                )
            )

        verify {
            passEventQueryDao.getAllPassEventSortedByDate(Instant.EPOCH)
            passEventQueryDao.getAllPassEventSortedByDate(firstCall.last().timestamp)
        }
        confirmVerified(passEventQueryDao)
    }
}