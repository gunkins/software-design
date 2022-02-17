package com.github.gunkins.vkstatistics

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.github.gunkins.vkstatistics.client.VkClientImpl
import com.github.gunkins.vkstatistics.model.NewsfeedItem
import com.github.gunkins.vkstatistics.model.NewsfeedSearchResponse
import com.github.gunkins.vkstatistics.model.VkResponse
import com.github.gunkins.vkstatistics.service.FeedStatisticService
import com.github.gunkins.vkstatistics.service.FeedStatisticServiceImpl
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

private const val HASHTAG = "#hashtag"
private val CURRENT_TIME = Instant.ofEpochSecond(100_000_000)
private val FIXED_CLOCK = Clock.fixed(CURRENT_TIME, ZoneId.systemDefault())

class FeedStatisticServiceMockEngineTest {
    private val jsonMapper = jsonMapper {
        addModule(KotlinModule())
        addModule(JavaTimeModule())
    }

    private val feedStatisticService: FeedStatisticService

    init {
        val data = listOf(
            NewsfeedItem(1, CURRENT_TIME.minusMinutes(10)),
            NewsfeedItem(2, CURRENT_TIME.minusMinutes(20)),
            NewsfeedItem(3, CURRENT_TIME.minusMinutes(50)),
            NewsfeedItem(4, CURRENT_TIME.minusMinutes(61)),
            NewsfeedItem(10, CURRENT_TIME.minusMinutes(121)),
            NewsfeedItem(100, CURRENT_TIME.minusMinutes(121)),
            NewsfeedItem(100500, CURRENT_TIME.minusMinutes(1000)),
        )
        val mockEngine = getMockEngineByData(data)
        feedStatisticService = FeedStatisticServiceImpl(VkClientImpl(mockEngine, ""), FIXED_CLOCK)
    }

    @Test
    fun `Test statistics for data`() {
        val expected = listOf(3, 1, 2, 0, 0).map { it.toLong() }
        val actual = feedStatisticService.getStatisticForQuery("#hashtag", 5)

        assertEquals(expected, actual)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun getMockEngineByData(items: List<NewsfeedItem>): HttpClientEngineFactory<MockEngineConfig> {
        return MockEngine.config {
            addHandler { request ->
                val count = request.url.parameters["count"]?.toInt() ?: Int.MAX_VALUE
                val startTime = instantOfNullableSeconds(request.url.parameters["start_time"]) ?: Instant.MIN
                val endTime = instantOfNullableSeconds(request.url.parameters["end_time"]) ?: Instant.MAX
                val query = request.url.parameters["q"]!!

                val filteredItems = items.filter { query == HASHTAG }.filter { it.date in startTime..endTime }
                val totalCount = filteredItems.size.toLong()
                val vkResponse = VkResponse(NewsfeedSearchResponse(filteredItems.take(count), totalCount))

                respond(
                    jsonMapper.writeValueAsString(vkResponse),
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }
    }

    private fun Instant.minusMinutes(n: Int) = this.minus(n.toLong(), ChronoUnit.MINUTES)

    private fun instantOfNullableSeconds(seconds: String?) = seconds?.toLong()?.let { Instant.ofEpochSecond(it) }
}
