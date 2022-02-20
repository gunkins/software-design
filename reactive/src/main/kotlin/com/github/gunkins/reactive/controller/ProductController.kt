package com.github.gunkins.reactive.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.model.Product
import com.github.gunkins.reactive.dto.ProductDto
import com.github.gunkins.reactive.service.ProductService

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping("/get")
    fun get(@RequestParam userId: String): Flux<ProductDto> {
        return productService.getAll(userId)
    }

    @PostMapping("/add")
    fun add(
        @RequestParam userId: String,
        @RequestBody productDto: ProductDto
    ): Mono<Product> {
        return productService.add(userId, productDto)
    }
}