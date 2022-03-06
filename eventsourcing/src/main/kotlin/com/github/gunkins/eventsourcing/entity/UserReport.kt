package com.github.gunkins.eventsourcing.entity

import java.time.Duration

data class UserReport(val userId: Long, val visits: Int, val allVisitTime: Duration)