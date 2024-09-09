package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.dto.CaptureStartRequest
import io.github.x1111101101.dataset.capture.service.CaptureService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.time.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.temporal.ChronoUnit

fun Routing.routeCaptures() {
    route("capture") {
        post("start") {
            println("start request")
            val json = call.receiveText()
            val request = Json.decodeFromString<CaptureStartRequest>(json)
            val response = CaptureService.startCaptureSession(request)
            call.respondText(Json.encodeToString(response))
        }
        sse("state/{ch}") {
            val channel = call.parameters["ch"]?.toIntOrNull()
            if(channel == null || channel !in 0..1) {
                return@sse
            }
            withTimeout(Duration.of(30, ChronoUnit.SECONDS)) {
                CaptureService.captureSessionState(channel).map {
                    send(Json.encodeToString(it))
                }.stateIn(this)
            }
        }
        post("upload") {  }
    }
}