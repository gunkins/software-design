package com.github.gunkins.eventsourcing.config

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class TestableClock : Clock() {
    private val system = systemDefaultZone()
    private var fixed: Clock? = null

    override fun getZone(): ZoneId {
        return (fixed ?: system).zone
    }

    override fun withZone(zone: ZoneId?): Clock {
        return (fixed ?: system).withZone(zone)
    }

    override fun instant(): Instant {
        return (fixed ?: system).instant()
    }

    fun setFixed(instant: Instant) {
        this.fixed = fixed(instant, ZoneId.systemDefault())
    }
}