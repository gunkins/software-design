package com.github.gunkins.exchange.routes

import com.github.gunkins.exchange.domain.service.ShareService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.shares() {
    val shareService by closestDI().instance<ShareService>()

    route("/share") {
        get("/all") {
            val shares = shareService.getShares()
            call.respond(shares)
        }

        get("/by-figi") {
            val figi = call.request.queryParameters.getOrFail("figi")
            val share = shareService.getShareOrThrow(figi)
            call.respond(share)
        }
    }
}