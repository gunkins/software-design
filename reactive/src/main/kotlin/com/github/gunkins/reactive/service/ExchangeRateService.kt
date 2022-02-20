package com.github.gunkins.reactive.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.model.Currency
import com.github.gunkins.reactive.dao.model.MoneyValue
import java.math.BigDecimal
import java.net.URI
import java.time.Duration

@Service
class ExchangeRateService(
    private val webClient: WebClient
) {
    private val exchangeRates: Cache<CurrencyExchange, BigDecimal> =
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10L))
            .build()

    fun convert(from: MoneyValue, to: Currency): Mono<BigDecimal> {
        return getRate(from.currency, to).map { rate -> rate * from.amount }
    }

    fun getRate(from: Currency, to: Currency): Mono<BigDecimal> {
        val cachedRate = exchangeRates.getIfPresent(CurrencyExchange(from, to))

        return if (cachedRate != null) {
            Mono.just(cachedRate)
        } else {
            webClient.get()
                .uri { builder -> builder.buildExchangeRateUri(from) }
                .retrieve()
                .bodyToMono(ExchangeRateResponse::class.java)
                .doOnNext(this::cacheResponse)
                .map { it.results.getValue(to) }
        }
    }

    private fun UriBuilder.buildExchangeRateUri(from: Currency): URI {
        val allCurrencies = Currency.values().joinToString(separator = ",") { it.name }

        return this.scheme("https")
            .host(EXCHANGE_RATE_API_HOST)
            .path(EXCHANGE_RATE_API_PATH)
            .queryParam("api_key", EXCHANGE_RATE_API_KEY)
            .queryParam("from", from.name)
            .queryParam("to", allCurrencies)
            .build()
    }

    private fun cacheResponse(response: ExchangeRateResponse) {
        response.results.forEach { (currency, rate) ->
            exchangeRates.put(CurrencyExchange(response.base, currency), rate)
        }
    }

    private data class CurrencyExchange(val from: Currency, val to: Currency)
    private data class ExchangeRateResponse(val base: Currency, val results: Map<Currency, BigDecimal>)

    companion object {
        private const val EXCHANGE_RATE_API_HOST = "api.fastforex.io"
        private const val EXCHANGE_RATE_API_PATH = "fetch-multi"
        private const val EXCHANGE_RATE_API_KEY = "" // TODO(Set token)
    }
}