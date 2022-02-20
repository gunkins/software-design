package com.github.gunkins.exchange.routes

import com.github.gunkins.exchange.domain.ShareQuantity
import com.github.gunkins.exchange.domain.TopUpBalanceRequest
import com.github.gunkins.exchange.domain.UserForm
import com.github.gunkins.exchange.domain.service.UserAccountService
import com.github.gunkins.exchange.domain.service.UserShareService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.accounts() {
    val userAccountService by closestDI().instance<UserAccountService>()
    val userShareService by closestDI().instance<UserShareService>()

    route("/user") {
        post("/register") {
            val form = call.receive<UserForm>()
            val userAccount = userAccountService.createAccount(form.fullName)

            call.respond(userAccount)
        }

        route("/{accountId}") {
            get("/portfolio") {
                val accountId = call.parameters.getAccountId()
                val portfolio = userShareService.getUserPortfolio(accountId)

                call.respond(portfolio)
            }

            post("/top-up") {
                val accountId = call.parameters.getAccountId()
                val amount = call.receive<TopUpBalanceRequest>().amount

                userAccountService.topUpBalance(accountId, amount)

                call.respond(HttpStatusCode.OK)
            }

            post("/buy") {
                val accountId = call.parameters.getAccountId()
                val shareQuantity = call.receive<ShareQuantity>()

                userShareService.buyShares(accountId, shareQuantity.figi, shareQuantity.quantity)

                call.respond(HttpStatusCode.OK)
            }

            post("/sell") {
                val accountId = call.parameters.getAccountId()
                val shareQuantity = call.receive<ShareQuantity>()

                userShareService.sellShares(accountId, shareQuantity.figi, shareQuantity.quantity)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private fun Parameters.getAccountId(): Long {
    val stringAccountId = getOrFail("accountId")

    return try {
        stringAccountId.toLong()
    } catch (e: NumberFormatException) {
        throw NotFoundException("Account $stringAccountId not found")
    }
}