package util

import com.github.gunkins.exchange.domain.Account
import com.github.gunkins.exchange.domain.Position
import com.github.gunkins.exchange.domain.Share
import com.github.gunkins.exchange.domain.dao.Shares
import com.github.gunkins.exchange.domain.dao.UserAccounts
import com.github.gunkins.exchange.domain.dao.UserShares
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

fun insertShare(share: Share) = transaction {
    Shares.insert {
        it[figi] = share.figi
        it[name] = share.name
        it[price] = share.price
        it[available] = share.available
    }
}

fun insertAccountAndGet(fullName: String, balance: BigDecimal): Account = transaction {
    val id = UserAccounts.insertAndGetId {
        it[this.fullName] = fullName
        it[this.balance] = balance
    }.value

    Account(id, fullName, balance)
}

fun insertUserShare(accountId: Long, figi: String, quantity: Long) = transaction {
    UserShares.insert {
        it[this.accountId] = accountId
        it[this.figi] = figi
        it[this.quantity] = quantity
    }
}

fun selectAllUsers(): List<Account> = transaction {
    UserAccounts.selectAll().map { UserAccounts.toDomain(it) }.toList()
}

