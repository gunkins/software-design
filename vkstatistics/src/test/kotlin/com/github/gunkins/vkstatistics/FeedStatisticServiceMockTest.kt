package com.github.gunkins.vkstatistics

import com.github.gunkins.vkstatistics.client.VkClient
import com.github.gunkins.vkstatistics.model.NewsfeedSearchResponse
import com.github.gunkins.vkstatistics.model.VkResponse
import com.github.gunkins.vkstatistics.service.FeedStatisticService
import com.github.gunkins.vkstatistics.service.FeedStatisticServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class FeedStatisticServiceMockTest {
    private val feedStatisticService: FeedStatisticService

    init {
        val emptyVkResponse = VkResponse(NewsfeedSearchResponse(listOf(), 0))
        val vkResponse = VkResponse(NewsfeedSearchResponse(listOf(), 5))

        val vkClient = mock<VkClient> {
            onBlocking {
                newsfeedSearch(any(), eq(0), any(), any())
            } doReturn emptyVkResponse

            onBlocking {
                newsfeedSearch(eq("#hashtag"), eq(0), any(), any())
            } doReturn vkResponse
        }

        feedStatisticService = FeedStatisticServiceImpl(vkClient)
    }

    @Test
    fun `Hashtag sign should be optional`() {
        val n = 3
        val withHashtagSign = feedStatisticService.getStatisticForHashtag("#hashtag", n)
        val withoutHashtagSign = feedStatisticService.getStatisticForHashtag("hashtag", n)

        assertEquals(listOf(5L, 5L, 5L), withHashtagSign)
        assertEquals(withHashtagSign, withoutHashtagSign)
    }

    @Test
    fun `Query and hashtag statistics should return the same result`() {
        val n = 5
        val hashtagStatistics = feedStatisticService.getStatisticForHashtag("#hashtag", n)
        val queryStatistics = feedStatisticService.getStatisticForQuery("#hashtag", n)

        assertEquals(hashtagStatistics, queryStatistics)
    }
}