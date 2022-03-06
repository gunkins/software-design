package com.github.gunkins.eventsourcing.integration

import com.github.gunkins.eventsourcing.config.CleanupEmbeddedDbExtension
import com.github.gunkins.eventsourcing.config.ClockConfig
import com.github.gunkins.eventsourcing.config.EmbeddedDbInitializeExtension
import com.github.gunkins.eventsourcing.config.TestableClock
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(CleanupEmbeddedDbExtension::class, EmbeddedDbInitializeExtension::class)
@ContextConfiguration(classes = [ClockConfig::class])
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY, refresh = AFTER_CLASS)
abstract class IntegrationTest {
    @Autowired
    protected lateinit var clock: TestableClock

    @Autowired
    protected lateinit var mockMvc: MockMvc
}