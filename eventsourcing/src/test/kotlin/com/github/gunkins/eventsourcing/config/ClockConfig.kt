package com.github.gunkins.eventsourcing.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import java.time.Clock

@TestConfiguration
class ClockConfig {
    @Bean
    fun clock(clock: TestableClock): Clock = clock

    @Bean
    fun clock(): TestableClock {
        return TestableClock()
    }
}