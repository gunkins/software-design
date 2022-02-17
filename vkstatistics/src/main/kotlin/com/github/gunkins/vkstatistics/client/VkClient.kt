package com.github.gunkins.vkstatistics.client

import com.github.gunkins.vkstatistics.model.NewsfeedSearchResponse
import com.github.gunkins.vkstatistics.model.VkResponse
import java.time.Instant

interface VkClient {
    val apiVersion: String

    suspend fun newsfeedSearch(
        query: String,
        count: Int? = null,
        startTime: Instant? = null,
        endTime: Instant? = null
    ): VkResponse<NewsfeedSearchResponse>
}