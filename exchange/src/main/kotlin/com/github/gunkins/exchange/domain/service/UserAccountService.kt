package com.github.gunkins.exchange.domain.service

import com.github.gunkins.exchange.domain.Account
import com.github.gunkins.exchange.domain.dao.UserAccountDao
import io.ktor.features.*
import java.math.BigDecimal

class UserAccountService(
    private val userAccountDao: UserAccountDao
) {
    fun getAccountOrThrow(accountId: Long): Account {
        return userAccountDao.find(accountId)
            ?: throw NotFoundException("User account with id $accountId not found")
    }

    fun createAccount(fullName: String): Account {
        val initialBalance = BigDecimal.ZERO
        val id = userAccountDao.insertAndGetId(fullName, initialBalance)
        return Account(id, fullName, initialBalance)
    }

    fun topUpBalance(accountId: Long, amount: BigDecimal) {
        userAccountDao.addAccountBalance(accountId, amount)
    }

    fun withdrawBalance(accountId: Long, amount: BigDecimal) {
        userAccountDao.addAccountBalance(accountId, amount.negate())
    }
}