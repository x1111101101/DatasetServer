package io.github.x1111101101.dataset

import io.github.x1111101101.dataset.capture.route.routeCaptures
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val mainScope = CoroutineScope(Executors.newScheduledThreadPool(10).asCoroutineDispatcher())

fun main() {
    embeddedServer(Netty, port = 5101) {
        install(SSE)
        module()
    }.start(wait = true)
}

private fun Application.module() {
    routing {
        val addr= "https://drive.google.com/drive/folders/1mozYJNeArRtVVBsKKUPpHaBSBz5b410G?usp=sharing"
        get {
            call.respondRedirect(addr)
        }
        routeCaptures()
    }
}