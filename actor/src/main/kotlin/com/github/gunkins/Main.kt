package com.github.gunkins

import com.github.gunkins.actors.MasterActor
import com.github.gunkins.actors.SearchTasks
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Props
import akka.actor.typed.SpawnProtocol
import akka.actor.typed.javadsl.AskPattern
import com.github.gunkins.client.OrganicResult
import com.github.gunkins.client.SearchResponse
import com.github.gunkins.client.SerpClient
import com.github.gunkins.client.SerpEngine
import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.slf4j.event.Level
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Supplier
import kotlin.system.measureTimeMillis

const val API_KEY = "demo"

fun main() {
    val client = SerpClient(API_KEY)
    val actorSystem: ActorSystem<SpawnProtocol.Command> = ActorSystem.create(SpawnProtocol.create(), "spawn")

    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        module { appModule(client, actorSystem) }

        connector { port = 8080 }
    }).start(wait = true)
}

fun Application.appModule(serpClient: SerpClient, actorSystem: ActorSystem<SpawnProtocol.Command>) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val resultsPerEngine = getConfigAsInt("results-per-engine")
    val masterReceiveTimeout = Duration.ofMillis(getConfigAsLong("actor.master.receive.timeout-ms"))
    val spawnTimeout = Duration.ofMillis(getConfigAsLong("actor.spawn.timeout-ms"))

    routing {
        get("/search") {
            val query = call.parameters.getOrFail("query")
            log.info("Executing search: $query")

            val tasks = getSearchTaskList(serpClient, query)
            val searchResultFuture = CompletableFuture<List<OrganicResult>>()
            val searchTasks = SearchTasks(tasks, masterReceiveTimeout, resultsPerEngine, searchResultFuture)

            val masterActor: CompletionStage<ActorRef<MasterActor.Command>> =
                spawnMasterActor(actorSystem, searchTasks, spawnTimeout)

            masterActor.await()
            val result: List<OrganicResult> = searchResultFuture.await()

            call.respond(HttpStatusCode.OK, result)
        }
    }
}

private fun Application.getSearchTaskList(
    serpClient: SerpClient,
    query: String
): List<Supplier<SearchResponse>> {
    return SerpEngine.values().map { engine ->
        Supplier {
            log.info("Executing search with engine $engine")
            val body: SearchResponse
            val time = measureTimeMillis {
                body = runBlocking { serpClient.search(engine, query) }
            }
            log.info("Finished executing search with engine $engine in ${time}ms")
            body
        }
    }
}

private fun spawnMasterActor(
    actorSystem: ActorSystem<SpawnProtocol.Command>,
    searchTasks: SearchTasks,
    spawnTimeout: Duration
): CompletionStage<ActorRef<MasterActor.Command>> {
    return AskPattern.ask(
        actorSystem,
        { replyTo -> SpawnProtocol.Spawn(MasterActor.create(searchTasks), "master", Props.empty(), replyTo) },
        spawnTimeout,
        actorSystem.scheduler()
    )
}

private fun Application.getConfigAsInt(path: String): Int {
    return environment.config.property(path).getString().toInt()
}

private fun Application.getConfigAsLong(path: String): Long {
    return environment.config.property(path).getString().toLong()
}