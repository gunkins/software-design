package com.github.gunkins.reactive.dao.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    val id: String? = null,
    val name: String,
    val currency: Currency
)