package com.github.gunkins.reactive.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate


@Configuration
class MongoConfig {
    @Value("\${spring.data.mongodb.uri}")
    private lateinit var uri: String

    @Value("\${spring.data.mongodb.database}")
    private lateinit var database: String

    @Bean
    fun mongoClient(): MongoClient = MongoClients.create(uri)

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate = ReactiveMongoTemplate(mongoClient(), database)
}