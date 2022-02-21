package com.github.gunkins.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResponse(
    @JsonProperty("request_info")
    val requestInfo: RequestInfo,
    @JsonProperty("organic_results")
    val organicResults: List<OrganicResult>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestInfo(val success: Boolean)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrganicResult(val title: String, val link: String)
