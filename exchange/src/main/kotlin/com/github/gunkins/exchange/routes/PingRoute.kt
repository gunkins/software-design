package com.github.gunkins.exchange.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.ping() {
    get("/ping") {
        call.respond(HttpStatusCode.OK)
    }
}