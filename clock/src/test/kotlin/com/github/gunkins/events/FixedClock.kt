package com.github.gunkins.events

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class FixedClock(initialInstant: Instant, initialZoneId: ZoneId) : Clock() {
    private var fixedClock = fixed(initialInstant, initialZoneId)

    override fun getZone(): ZoneId = fixedClock.zone

    override fun withZone(zone: ZoneId): Clock = fixedClock.withZone(zone)

    override fun instant(): Instant = fixedClock.instant()

    fun setFixed(instant: Instant, zoneId: ZoneId) {
        fixedClock = fixed(instant, zoneId)
    }
}