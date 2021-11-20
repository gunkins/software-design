package com.github.gunkins.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.test.assertNotNull

class EventStatisticTest {
    private val clock = FixedClock(Instant.ofEpochMilli(0L), ZoneId.systemDefault())

    @BeforeEach
    fun resetClock() {
        clock.setFixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault())
    }

    @Test
    fun `No events happened`() = testEventStatistic { eventStatistic ->
        val eventRpm = eventStatistic.getEventStatisticByName("event")
        val allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(0.0, eventRpm)
        assertDoublesAreEqual(0.0, allEventRpm["event"] ?: 0.0)
    }

    @Test
    fun `Happened one event`() = testEventStatistic { eventStatistic ->
        eventStatistic.incEvent("event")

        val eventRpm = eventStatistic.getEventStatisticByName("event")
        val anotherEventRpm = eventStatistic.getEventStatisticByName("some-event")
        val allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(1.0 / MINUTES_IN_HOUR, eventRpm)
        assertDoublesAreEqual(0.0, anotherEventRpm)
        assertDoublesAreEqual(eventRpm, allEventRpm["event"])
    }

    @Test
    fun `Event occurs multiple times at the same time`() = testEventStatistic { eventStatistic ->
        val iterations = 10
        repeat(iterations) {
            eventStatistic.incEvent("event")
        }

        val eventRpm = eventStatistic.getEventStatisticByName("event")
        val allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(iterations.toDouble() / MINUTES_IN_HOUR, eventRpm)
        assertDoublesAreEqual(eventRpm, allEventRpm["event"])
    }

    @Test
    fun `Event occurs multiple times at intervals`() = testEventStatistic { eventStatistic ->
        val iterations = 100
        repeat(iterations) {
            eventStatistic.incEvent("event")
            clock.increaseTimeBy(30, ChronoUnit.MILLIS)
        }

        val eventRpm = eventStatistic.getEventStatisticByName("event")
        val allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(iterations.toDouble() / MINUTES_IN_HOUR, eventRpm)
        assertDoublesAreEqual(eventRpm, allEventRpm["event"])
    }

    @Test
    fun `Multiple events occur multiple times at the same time`() = testEventStatistic { eventStatistic ->
        val iterations = 10
        val eventNames = listOf("event1", "event2", "event3")
        repeat(iterations) {
            for (eventName in eventNames) {
                eventStatistic.incEvent(eventName)
            }
        }

        val expectedRpm = iterations.toDouble() / MINUTES_IN_HOUR
        for (eventName in eventNames) {
            assertDoublesAreEqual(expectedRpm, eventStatistic.getEventStatisticByName(eventName))
            assertDoublesAreEqual(expectedRpm, eventStatistic.getAllEventStatistic()[eventName])
        }
    }

    @Test
    fun `Multiple events occur multiple times at intervals`() = testEventStatistic { eventStatistic ->
        val iterations = 10
        val eventNames = listOf("event1", "event2", "event3")
        repeat(iterations) {
            for (event in eventNames) {
                eventStatistic.incEvent(event)
                clock.increaseTimeBy(10, ChronoUnit.MILLIS)
            }
        }

        val expectedRpm = iterations.toDouble() / MINUTES_IN_HOUR
        for (event in eventNames) {
            assertDoublesAreEqual(expectedRpm, eventStatistic.getEventStatisticByName(event))
            assertDoublesAreEqual(expectedRpm, eventStatistic.getAllEventStatistic()[event])
        }
    }

    @Test
    fun `Events happened more than hour ago should not be counted`() = testEventStatistic { eventStatistic ->
        repeat(10) {
            eventStatistic.incEvent("event")
            clock.increaseTimeBy(10, ChronoUnit.MINUTES)
        }

        var expectedRpm = 6.0 / MINUTES_IN_HOUR
        var eventRpm = eventStatistic.getEventStatisticByName("event")
        var allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(expectedRpm, eventRpm)
        assertDoublesAreEqual(expectedRpm, allEventRpm["event"])

        clock.increaseTimeBy(1, ChronoUnit.MILLIS)

        expectedRpm = 5.0 / MINUTES_IN_HOUR
        eventRpm = eventStatistic.getEventStatisticByName("event")
        allEventRpm = eventStatistic.getAllEventStatistic()

        assertDoublesAreEqual(expectedRpm, eventRpm)
        assertDoublesAreEqual(expectedRpm, allEventRpm["event"])

    }

    @Test
    fun `Multiple events happened more than hour ago should not be counted`() = testEventStatistic { eventStatistic ->
        val eventNames = listOf("event1", "event2", "event3", "event4", "event5")
        repeat(10) {
            for (event in eventNames) {
                eventStatistic.incEvent(event)
                clock.increaseTimeBy(2, ChronoUnit.MINUTES)
            }
        }

        val expectedRpm = 6.0 / MINUTES_IN_HOUR

        for (event in eventNames) {
            assertDoublesAreEqual(expectedRpm, eventStatistic.getEventStatisticByName(event))
            assertDoublesAreEqual(expectedRpm, eventStatistic.getAllEventStatistic()[event])
        }
    }

    private fun testEventStatistic(test: (EventStatistic) -> Unit) {
        EventStatisticImpl(clock).apply(test)
    }

    private fun assertDoublesAreEqual(expected: Double, actual: Double?) {
        assertNotNull(actual)
        assertEquals(expected, actual, EPSILON)
    }

    private fun FixedClock.increaseTimeBy(amountToAdd: Int, temporalUnit: TemporalUnit) {
        setFixed(instant().plus(amountToAdd.toLong(), temporalUnit), zone)
    }

    companion object {
        private const val EPSILON = 1e-10
        private const val MINUTES_IN_HOUR = 60
    }
}