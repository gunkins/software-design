package com.github.gunkins.eventsourcing.config

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource


class CleanupEmbeddedDbExtension : AfterEachCallback {
    override fun afterEach(context: ExtensionContext) {
        val dataSource = SpringExtension.getApplicationContext(context).getBean("dataSource", DataSource::class)
        truncateTables(dataSource as DataSource)
    }

    private fun truncateTables(dataSource: DataSource) {
        val scriptLauncher = ResourceDatabasePopulator()
        scriptLauncher.addScript(ClassPathResource("truncate.sql"))
        scriptLauncher.execute(dataSource)
    }
}