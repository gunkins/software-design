package com.github.gunkins.reactive.dao.model

import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document
data class Product(val description: String, val moneyValue: MoneyValue)

data class MoneyValue(val amount: BigDecimal, val currency: Currency)