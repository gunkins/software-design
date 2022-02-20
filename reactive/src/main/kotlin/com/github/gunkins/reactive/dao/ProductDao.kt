package com.github.gunkins.reactive.dao

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.model.Product

@Repository
class ProductDao(
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun insert(product: Product): Mono<Product> {
        return mongoTemplate.insert(product)
    }

    fun findAll(): Flux<Product> {
        return mongoTemplate.findAll(Product::class.java)
    }
}