package com.github.gunkins.exchange.domain

import java.math.BigDecimal

data class Account(
    val id: Long,
    val fullName: String,
    val balance: BigDecimal
)

data class UserForm(val fullName: String)

data class TopUpBalanceRequest(val amount: BigDecimal)