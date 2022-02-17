package com.github.gunkins.vkstatistics

import com.github.gunkins.vkstatistics.client.VkClientImpl
import com.github.gunkins.vkstatistics.model.NewsfeedItem
import com.github.gunkins.vkstatistics.model.NewsfeedSearchResponse
import com.github.gunkins.vkstatistics.model.VkResponse
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class VkClientMockEngineTest {

    @Test
    fun `Client should parse empty vk response`(): Unit = runBlocking {
        val emptyResponse = readFromResources("/empty-response.json")

        val vkClient = VkClientImpl(getMockEngineByResponse(emptyResponse), "")

        val expected = VkResponse(NewsfeedSearchResponse(emptyList(), 0L))
        val actual = vkClient.newsfeedSearch("any query")

        assertEquals(expected, actual)
    }

    @Test
    fun `Client should parse response with items`(): Unit = runBlocking {
        val response = readFromResources("/response-with-items.json")

        val vkClient = VkClientImpl(getMockEngineByResponse(response), "")

        val expected = VkResponse(
            NewsfeedSearchResponse(
                listOf(
                    NewsfeedItem(3, Instant.ofEpochSecond(123)),
                    NewsfeedItem(4, Instant.ofEpochSecond(124)),
                    NewsfeedItem(5, Instant.ofEpochSecond(125)),
                ),
                3
            )
        )
        val actual = vkClient.newsfeedSearch("any query")

        assertEquals(expected, actual)
    }

    private fun getMockEngineByResponse(response: String): HttpClientEngineFactory<MockEngineConfig> {
        return MockEngine.config {
            addHandler {
                respond(
                    response,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }
    }

    private fun readFromResources(resource: String): String =
        javaClass.getResource(resource)?.readText()
            ?: throw IllegalArgumentException("Resource '$resource' not found")
}