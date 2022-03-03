package com.github.gunkins.exchange

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.gunkins.exchange.config.DependencyConfig
import com.github.gunkins.exchange.config.initDatabase
import com.github.gunkins.exchange.config.initTables
import com.github.gunkins.exchange.domain.service.InvestApiService
import com.github.gunkins.exchange.routes.accounts
import com.github.gunkins.exchange.routes.admin
import com.github.gunkins.exchange.routes.ping
import com.github.gunkins.exchange.routes.shares
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di


fun main(args: Array<String>) {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        initDatabase(config)
        initTables()

        val di = DI {
            import(DependencyConfig.investApiClient(config.property("ktor.invest-api.token").getString()))
            import(DependencyConfig.mainModule())
        }
        module { appModule(di) }

        connector { port = 8080 }
    }).start(wait = true)
}

fun Application.appModule(di: DI) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        }
    }
    install(CallLogging)
    install(StatusPages) {
        exception<NotFoundException> { cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
    }

    di { extend(di) }

    val investApiService by closestDI().instance<InvestApiService>()
    investApiService.subscribeOnAddedFigiPriceUpdate()

    routing {
        ping()
        admin()
        accounts()
        shares()
    }
}

