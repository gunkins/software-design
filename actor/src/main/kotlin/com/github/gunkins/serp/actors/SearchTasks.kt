package com.github.gunkins.serp.actors

import com.github.gunkins.serp.client.OrganicResult
import com.github.gunkins.serp.client.SearchResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

data class SearchTasks(
    val task: List<Supplier<SearchResponse>>,
    val timeout: Duration,
    val resultsPerEngine: Int,
    val resultFuture: CompletableFuture<List<OrganicResult>>
)