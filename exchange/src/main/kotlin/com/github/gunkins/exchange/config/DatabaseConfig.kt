package com.github.gunkins.exchange.config

import com.github.gunkins.exchange.domain.dao.Shares
import com.github.gunkins.exchange.domain.dao.UserAccounts
import com.github.gunkins.exchange.domain.dao.UserShares
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun initDatabase(applicationConfig: ApplicationConfig) {
    val dataSource = HikariDataSource(hikari(applicationConfig))
    val db = Database.connect(dataSource)
    TransactionManager.defaultDatabase = db

    LoggerFactory.getLogger(Application::class.simpleName).info("Initialized Database")
}

fun initTables() = transaction {
    SchemaUtils.create(Shares, UserAccounts, UserShares)
}

private fun hikari(applicationConfig: ApplicationConfig): HikariConfig {
    val hikariConfig = HikariConfig()
    hikariConfig.username = applicationConfig.getHikariProperty("user")
    hikariConfig.password = applicationConfig.getHikariProperty("password")
    hikariConfig.jdbcUrl = applicationConfig.getHikariProperty("jdbcUrl")
    hikariConfig.maximumPoolSize = applicationConfig.getHikariProperty("maximumPoolSize").toInt()
    hikariConfig.validate()
    return hikariConfig
}

private fun ApplicationConfig.getHikariProperty(path: String): String {
    return config("ktor.hikari").property(path).getString()
}

