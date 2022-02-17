package com.github.gunkins.vkstatistics

import com.github.gunkins.vkstatistics.client.VkClientImpl
import com.github.gunkins.vkstatistics.service.FeedStatisticServiceImpl
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*

fun main() {
    val accessToken = ""

    val vkClient = VkClientImpl(CIO, accessToken) {
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    val feedStatisticService = FeedStatisticServiceImpl(vkClient)

    println(feedStatisticService.getStatisticForHashtag("#Выборы", 20))
}