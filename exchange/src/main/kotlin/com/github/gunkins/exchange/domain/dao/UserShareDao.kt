package com.github.gunkins.exchange.domain.dao

import com.github.gunkins.exchange.domain.Position
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal object UserShares : Table("user_shares") {
    val accountId = long("account_id").references(UserAccounts.id)
    val figi = char("figi", 12).references(Shares.figi)
    val quantity = long("quantity")

    override val primaryKey = PrimaryKey(accountId, figi)
}

class UserShareDao {
    fun findUserShareQuantity(accountId: Long, figi: String): Long? = transaction {
        UserShares
            .select { (UserShares.accountId eq accountId) and (UserShares.figi eq figi) }
            .map { it[UserShares.quantity] }
            .singleOrNull()
    }

    fun findUserPositions(accountId: Long): List<Position> = transaction {
        (UserShares innerJoin Shares)
            .select { (UserShares.accountId eq accountId) and (UserShares.quantity greater 0) }
            .map {
                Position(
                    figi = it[UserShares.figi],
                    quantity = it[UserShares.quantity],
                    currentPrice = it[Shares.price]
                )
            }.toList()
    }

    fun addQuantityOrInsert(accountId: Long, figi: String, quantityToAdd: Long) = transaction {
        val arguments = listOf(
            UserShares.accountId to accountId,
            UserShares.figi to figi,
            UserShares.quantity to quantityToAdd,
        ).map { (column, arg) -> column.columnType to arg }

        val sql = """
            insert into user_shares (account_id, figi, quantity)
            values (?, ?, ?) 
            on conflict (account_id, figi)
            do update set quantity = user_shares.quantity + excluded.quantity
        """.trimIndent()

        exec(sql, arguments)
    }
}