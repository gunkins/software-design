package com.github.gunkins.vkstatistics.service

interface FeedStatisticService {
    fun getStatisticForHashtag(hashtag: String, n: Int): List<Long>
    fun getStatisticForQuery(query: String, n: Int): List<Long>
}