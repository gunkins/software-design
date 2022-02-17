package com.github.gunkins.vkstatistics.client

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.gunkins.vkstatistics.model.NewsfeedSearchResponse
import com.github.gunkins.vkstatistics.model.VkResponse
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import java.time.Instant

private const val API_VERSION = "5.131"

class VkClientImpl(
    engine: HttpClientEngineFactory<HttpClientEngineConfig>,
    private val accessToken: String,
    httpClientConfig: HttpClientConfig<HttpClientEngineConfig>.() -> Unit = {}
) : VkClient {
    override val apiVersion = API_VERSION

    private val httpClient = HttpClient(engine) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                registerModule(JavaTimeModule())
            }
        }
        httpClientConfig.invoke(this)
    }

    override suspend fun newsfeedSearch(
        query: String,
        count: Int?,
        startTime: Instant?,
        endTime: Instant?
    ): VkResponse<NewsfeedSearchResponse> {

        val response: VkResponse<NewsfeedSearchResponse> =
            httpClient.get("https://api.vk.com/method/newsfeed.search") {
                parameter("q", query)
                parameter("access_token", accessToken)
                parameter("v", API_VERSION)
                parameter("count", count)
                parameter("start_time", startTime?.epochSecond)
                parameter("end_time", endTime?.epochSecond)
            }
        return response
    }
}
