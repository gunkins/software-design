package com.github.gunkins.vkstatistics.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewsfeedSearchResponse(
    val items: List<NewsfeedItem>,

    @JsonProperty("total_count")
    val totalCount: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewsfeedItem(
    val id: Long,
    val date: Instant
)
