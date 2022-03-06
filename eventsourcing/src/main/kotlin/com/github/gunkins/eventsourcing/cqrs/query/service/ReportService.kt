package com.github.gunkins.eventsourcing.cqrs.query.service

import com.github.gunkins.eventsourcing.entity.PassEventType
import com.github.gunkins.eventsourcing.cqrs.query.dao.PassEventQueryDao
import com.github.gunkins.eventsourcing.entity.UserReport
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

@Service
class ReportService(
    private val passEventQueryDao: PassEventQueryDao
) {
    private val log = LoggerFactory.getLogger(ReportService::class.java)

    private val visitsPerDay = ConcurrentHashMap<LocalDate, Int>()
    private val userStats = ConcurrentHashMap<Long, UserStats>()

    private val lastUserEntrance = mutableMapOf<Long, Instant>()
    private var lastReadEventTime = Instant.EPOCH

    @Scheduled(fixedDelay = 60_000, initialDelay = 0)
    fun scheduledUpdateData() {
        updateData()
        log.info("Scheduled update data!")
    }

    private fun updateData() {
        val events = passEventQueryDao.getAllPassEventSortedByDate(lastReadEventTime)

        events.forEach {
            when (it.type) {
                PassEventType.ENTRANCE -> {
                    lastUserEntrance[it.userId] = it.timestamp

                    val date = LocalDate.ofInstant(it.timestamp, ZoneOffset.UTC)
                    visitsPerDay.merge(date, 1, Int::plus)
                }
                PassEventType.EXIT -> {
                    val userEntrance = lastUserEntrance[it.userId] ?: return@forEach
                    val userExit = it.timestamp
                    val visitTime = Duration.between(userEntrance, userExit)

                    userStats.compute(it.userId) { _, oldStats ->
                        if (oldStats == null) {
                            UserStats(1, visitTime)
                        } else {
                            UserStats(oldStats.visits + 1, oldStats.allVisitTime + visitTime)
                        }
                    }

                    lastUserEntrance.remove(it.userId)
                }
            }
        }

        if (events.isNotEmpty()) {
            lastReadEventTime = events.last().timestamp
        }
    }

    fun getVisits(from: LocalDate, to: LocalDate): Map<LocalDate, Int> {
        return visitsPerDay
            .filter { (date, _) -> date in from..to }
    }

    fun getUserReport(userId: Long): UserReport {
        return userStats[userId]
            ?.let {
                UserReport(userId, it.visits, it.allVisitTime)
            } ?: UserReport(userId, 0, Duration.ZERO)
    }

    private data class UserStats(val visits: Int, val allVisitTime: Duration)
}