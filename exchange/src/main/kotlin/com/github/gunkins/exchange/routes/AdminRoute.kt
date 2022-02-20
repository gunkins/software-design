package com.github.gunkins.exchange.routes

import com.github.gunkins.exchange.domain.ShareQuantity
import com.github.gunkins.exchange.domain.dao.UserShares.quantity
import com.github.gunkins.exchange.domain.service.ShareService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.admin() {
    val shareService by closestDI().instance<ShareService>()

    route("/admin") {
        post("/add-share")  {
            val shareQuantity = call.receive<ShareQuantity>()
            val share = shareService.addShareToExchange(shareQuantity.figi, shareQuantity.quantity)

            call.respond(share)
        }

        post("/add-quantity") {
            val shareQuantity = call.receive<ShareQuantity>()
            shareService.increaseAvailableShares(shareQuantity.figi, shareQuantity.quantity)

            call.respond(HttpStatusCode.OK)
        }
    }
}