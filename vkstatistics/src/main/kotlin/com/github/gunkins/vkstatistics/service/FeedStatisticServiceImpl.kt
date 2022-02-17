package com.github.gunkins.vkstatistics.service

import com.github.gunkins.vkstatistics.client.VkClient
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

class FeedStatisticServiceImpl(
    private val vkClient: VkClient,
    private val clock: Clock = Clock.systemDefaultZone()
) : FeedStatisticService {

    override fun getStatisticForHashtag(hashtag: String, n: Int): List<Long> {
        if (hashtag.isEmpty() || hashtag == "#") {
            throw IllegalArgumentException("Hashtag mustn't be empty")
        }

        val query = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
        return getStatisticForQuery(query, n)
    }

    override fun getStatisticForQuery(query: String, n: Int): List<Long> = runBlocking {
        val now = Instant.now(clock)
        (0 until n)
            .map { hours ->
                val to = now.minusHours(hours)
                val from = to.minusHours(1).plusMillis(1)

                async {
                    val vkResponse = vkClient.newsfeedSearch(query, count = 0, startTime = from, endTime = to)
                    vkResponse.response.totalCount
                }
            }
            .map { it.await() }
    }

    private fun Instant.minusHours(n: Int) = this.minus(n.toLong(), ChronoUnit.HOURS)
}