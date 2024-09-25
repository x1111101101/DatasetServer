package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.dto.instruction.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureSavedReport
import io.github.x1111101101.dataset.capture.service.CaptureService
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.util.cio.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
            val connectionPoint = call.request.origin
            val ip = connectionPoint.localAddress
            try {
                println("$ip: SSE")
                val channel = call.parameters["ch"]?.toIntOrNull()
                if (channel == null || channel !in 0..1) {
                    println("INVALID: $channel")
                    return@sse
                }
                send(Json.encodeToString(CaptureService.captureSessionState(channel)))
                launch {
                    CaptureService.captureSessionStateAsFlow(channel).collect {
                        send(Json.encodeToString(it))
                    }
                }
                launch {
                    while (isActive) {
                        delay(3000)

                    }
                }

                println("$ip: SSE close")

            } catch (_: SocketException) {
            } catch (_: ChannelWriteException) {
            } catch (_: IOException) {
            }
            println("$ip: SSE close complete")
            close()
        }
        sse("capturestate") {
            val connectionPoint = call.request.origin
            val ip = connectionPoint.localAddress
            try {
                println("$ip: SSE")
                val channel = call.parameters["ch"]?.toIntOrNull()
                if (channel == null || channel !in 0..1) {
                    println("INVALID: $channel")
                    return@sse
                }
                send(Json.encodeToString(CaptureService.captureSessionState(channel)))
                CaptureService.captureSessionStateAsFlow(channel).collect {
                    send(Json.encodeToString(it))
                }
            } catch (_: SocketException) {
            } catch (_: ChannelWriteException) {
            } catch (_: IOException) {
            }
            println("$ip: SSE close complete")
            close()
        }
        post("upload") {
            val json = call.request.queryParameters["request"]!!
            val request = Json.decodeFromString<CaptureSavedReport>(json)
            CaptureService.uploadCapture(request, call.receiveStream())
            call.respond(HttpStatusCode.OK)
        }
    }
}