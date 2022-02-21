package com.github.gunkins.serp.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*

class SerpClient(private val apiKey: String) {
    private val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    suspend fun search(engine: SerpEngine, query: String): SearchResponse {
        return httpClient.get("https://api.serpwow.com/live/search") {
            parameter("api_key", apiKey)
            parameter("engine", engine)
            parameter("q", query)
        }
    }
}
