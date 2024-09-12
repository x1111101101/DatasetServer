package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.dto.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.CaptureUploadRequest
import io.github.x1111101101.dataset.capture.service.CaptureService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
            println("SSE: ${call.request.userAgent()}")
            val channel = call.parameters["ch"]?.toIntOrNull()
            if(channel == null || channel !in 0..1) {
                println("INVALID")
                return@sse
            }
            CaptureService.captureSessionStateAsFlow(channel).map {
                send(Json.encodeToString(it))
            }.stateIn(this)
            println("SSE close: ${call.request.userAgent()}")
        }
        get("currentstate/{ch}") {
            val channel = call.parameters["ch"]?.toIntOrNull()
            if(channel == null || channel !in 0..1) {
                return@get
            }
            call.respondText(Json.encodeToString(CaptureService.captureSessionState(channel)))
        }
        post("upload") {
            println("upload request")
            val json = call.receiveText()
            val request = Json.decodeFromString<CaptureUploadRequest>(json)
            CaptureService.uploadCapture(request)
            call.respond(HttpStatusCode.OK)
        }
        post("snapshot") {

        }
    }
}