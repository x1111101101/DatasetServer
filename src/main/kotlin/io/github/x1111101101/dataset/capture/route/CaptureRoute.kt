package io.github.x1111101101.dataset.capture.route

import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.delay

fun Routing.routeCaptures() {
    route("capture") {
        sse("session/{ch}") {
            println("sse start")
            val channel = call.parameters["ch"]?.toIntOrNull()
            if(channel == null || channel !in 0..1) {
                return@sse
            }
            repeat(10) {
                delay(1000)
                send(ServerSentEvent("TEST: ${it}"))
            }
            println("sse end")
        }
    }
}