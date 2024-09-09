package io.github.x1111101101.dataset

import io.github.x1111101101.dataset.capture.route.routeCaptures
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*

fun main() {
    embeddedServer(Netty, port = 5101) {
        install(SSE)
        module()
    }.start(wait = true)
}

private fun Application.module() {
    routing {
        get {
            call.respondText("HELLO")
        }
        routeCaptures()
    }
}