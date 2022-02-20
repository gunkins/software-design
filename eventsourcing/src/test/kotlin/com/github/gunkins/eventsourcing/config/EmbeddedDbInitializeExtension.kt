package com.github.gunkins.eventsourcing.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource

class EmbeddedDbInitializeExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        val dataSource = SpringExtension.getApplicationContext(context).getBean("dataSource", DataSource::class)
        initSchema(dataSource as DataSource)
    }

    private fun initSchema(dataSource: DataSource) {
        val scriptLauncher = ResourceDatabasePopulator()
        scriptLauncher.addScript(ClassPathResource("schema.sql"))
        scriptLauncher.execute(dataSource)
    }
}