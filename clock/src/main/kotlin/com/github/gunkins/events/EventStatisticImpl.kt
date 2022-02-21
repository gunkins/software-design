package com.github.gunkins.events

import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

private typealias EventsList = LinkedList<Instant>

class EventStatisticImpl(
    private val clock: Clock
) : EventStatistic {
    private val eventsByName: MutableMap<String, EventsList> = mutableMapOf()

    override fun incEvent(name: String) {
        val now = Instant.now(clock)

        eventsByName.compute(name) { _, eventsList: EventsList? ->
            val list = eventsList ?: LinkedList()
            list.dropOldEvents(now)
            list += now
            list
        }
    }

    override fun getEventStatisticByName(name: String): Double {
        val now = Instant.now(clock)

        val eventsList = eventsByName.computeIfPresent(name) { _, eventsList: EventsList ->
            eventsList.dropOldEvents(now)
        } ?: return 0.0

        return eventsList.count().toDouble() / MINUTES_IN_HOUR
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        val now = Instant.now(clock)

        return eventsByName.mapValues { (_, eventsList) ->
            eventsList.dropOldEvents(now)
            eventsList.count().toDouble() / MINUTES_IN_HOUR
        }
    }

    override fun printStatistic() {
        getAllEventStatistic().forEach { (name, statistic) ->
            println("$name: $statistic")
        }
    }

    private fun EventsList.dropOldEvents(now: Instant): EventsList {
        val threshold = now.minus(1L, ChronoUnit.HOURS)

        while (isNotEmpty() && first.isBefore(threshold)) {
            removeFirst()
        }

        return this
    }

    companion object {
        private const val MINUTES_IN_HOUR = 60
    }
}