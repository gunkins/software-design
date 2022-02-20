package com.github.gunkins.reactive.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.github.gunkins.reactive.dao.ProductDao
import com.github.gunkins.reactive.dao.model.Currency
import com.github.gunkins.reactive.dao.model.MoneyValue
import com.github.gunkins.reactive.dao.model.Product
import com.github.gunkins.reactive.dto.ProductDto

@Service
class ProductService(
    private val userService: UserService,
    private val exchangeRateService: ExchangeRateService,
    private val productDao: ProductDao
) {
    fun getAll(userId: String): Flux<ProductDto> {
        return userService.getUser(userId)
            .flatMapMany { user ->
                getProductDtosWithCurrency(user.currency)
            }
    }

    fun add(userId: String, productDto: ProductDto): Mono<Product> {
        return userService.getUser(userId)
            .flatMap { user ->
                val product = Product(productDto.description, MoneyValue(productDto.value, user.currency))
                productDao.insert(product)
            }
    }

    private fun getProductDtosWithCurrency(targetCurrency: Currency): Flux<ProductDto> {
        return productDao.findAll()
            .flatMap { product ->
                exchangeRateService.convert(product.moneyValue, targetCurrency)
                    .map { value -> ProductDto(product.description, value) }
            }
    }
}