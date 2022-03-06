package com.github.gunkins.eventsourcing.controller

import com.github.gunkins.eventsourcing.cqrs.query.service.ReportService
import com.github.gunkins.eventsourcing.entity.UserReport
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/report")
class ReportController(
    private val reportService: ReportService
) {
    @GetMapping("/visits")
    fun getVisits(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ): Map<LocalDate, Int> {
        return reportService.getVisits(from, to)
    }

    @GetMapping("/{userId}")
    fun getUserReport(@PathVariable userId: Long): UserReport {
        return reportService.getUserReport(userId)
    }
}