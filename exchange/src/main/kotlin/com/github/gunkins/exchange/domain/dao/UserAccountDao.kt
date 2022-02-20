package com.github.gunkins.exchange.domain.dao

import com.github.gunkins.exchange.domain.Account
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal

internal object UserAccounts : LongIdTable("user_accounts") {
    val fullName = varchar("full_name", 100)
    val balance = decimal("balance", 20, 9)

    fun toDomain(row: ResultRow): Account {
        return Account(
            id = row[id].value,
            fullName = row[fullName],
            balance = row[balance]
        )
    }
}

class UserAccountDao {
    fun insertAndGetId(fullName: String, balance: BigDecimal): Long = transaction {
        UserAccounts.insertAndGetId {
            it[UserAccounts.fullName] = fullName
            it[UserAccounts.balance] = balance
        }.value
    }

    fun find(accountId: Long): Account? = transaction {
        UserAccounts
            .select { UserAccounts.id eq accountId }
            .map { UserAccounts.toDomain(it) }
            .singleOrNull()
    }

    fun addAccountBalance(id: Long, amountToAdd: BigDecimal) = transaction {
        UserAccounts.update({ UserAccounts.id eq id }) {
            with(SqlExpressionBuilder) {
                it[balance] = balance + amountToAdd
            }
        }
    }
}