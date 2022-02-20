package com.github.gunkins.eventsourcing.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class CommonConfiguration {
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}