package com.github.gunkins.exchange.domain

import java.math.BigDecimal

data class Portfolio(val account: Account, val positions: List<Position>)

data class Position(val figi: String, val quantity: Long, val currentPrice: BigDecimal)