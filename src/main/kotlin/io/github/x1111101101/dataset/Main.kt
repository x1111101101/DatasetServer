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
            call.respondText("https://drive.google.com/file/d/1c0H1Jh386OB5mjydoUc8Sa2LEctqh5Fp/view?usp=sharing")
        }
        routeCaptures()
    }
}