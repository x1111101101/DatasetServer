package io.github.x1111101101.dataset

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 5050) {
        module()
    }.start(wait = true)
}

private fun Application.module() {
    routing {
        get {
            call.respondText("HELLO")
        }
    }
}