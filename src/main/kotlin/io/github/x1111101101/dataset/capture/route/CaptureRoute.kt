package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.dto.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.CaptureUploadRequest
import io.github.x1111101101.dataset.capture.service.CaptureService
import io.github.x1111101101.dataset.mainScope
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.util.cio.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.SocketException

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
            try {
                println("SSE: ${call.request.userAgent()}")
                val channel = call.parameters["ch"]?.toIntOrNull()
                if (channel == null || channel !in 0..1) {
                    println("INVALID: $channel")
                    return@sse
                }
                send(Json.encodeToString(CaptureService.captureSessionState(channel)))
                CaptureService.captureSessionStateAsFlow(channel).map {
                    send(Json.encodeToString(it))
                }.stateIn(this)
                println("SSE close: ${call.request.userAgent()}")

            } catch (_: SocketException) {
            } catch (_: ChannelWriteException) {
            } catch (_: IOException) { }
        }
        get("currentstate/{ch}") {
            val channel = call.parameters["ch"]?.toIntOrNull()
            if (channel == null || channel !in 0..1) {
                return@get
            }
            call.respondText(Json.encodeToString(CaptureService.captureSessionState(channel)))
        }
        post("upload") {
            val json = call.request.queryParameters["request"]!!
            val request = Json.decodeFromString<CaptureUploadRequest>(json)
            CaptureService.uploadCapture(request, call.receiveStream())
            call.respond(HttpStatusCode.OK)
        }
    }
}