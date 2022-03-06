package com.github.gunkins.eventsourcing

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneOffset.UTC)

fun instant(timestamp: String): Instant {
    return Instant.from(formatter.parse(timestamp))
}
