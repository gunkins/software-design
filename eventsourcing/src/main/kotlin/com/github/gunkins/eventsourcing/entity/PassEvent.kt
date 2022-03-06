package com.github.gunkins.eventsourcing.entity

import java.time.Instant

data class PassEvent(val userId: Long, val timestamp: Instant, val type: PassEventType)