package io.github.x1111101101.dataset.capture.route

import io.github.x1111101101.dataset.capture.dto.instruction.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureSavedReport
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureUploadStartRequest
import io.github.x1111101101.dataset.capture.dto.instruction.WorkerCaptureUploadRequest
import io.github.x1111101101.dataset.capture.service.CaptureService
import io.github.x1111101101.dataset.mainScope
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.util.*
import io.ktor.util.cio.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.SocketException
import java.util.UUID

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
                    println("invalid channel id: $channel")
                    close()
                    return@sse
                }
                CaptureService.getChannelInstructionStateFlow(channel).collect {
                    send(Json.encodeToString(it))
                }
            } catch (_: SocketException) {
            } catch (_: ChannelWriteException) {
            } catch (_: IOException) {}
            println("$ip: SSE close complete")
            close()
        }
        sse("capturestate/{ch}") {
            val connectionPoint = call.request.origin
            val ip = connectionPoint.localAddress
            try {
                println("$ip: SSE")
                val channel = call.parameters["ch"]?.toIntOrNull()
                if (channel == null || channel !in 0..1) {
                    println("invalid channel id: $channel")
                    close()
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
        post("save") {
            val json = call.receiveText()
            val request = Json.decodeFromString<CaptureSavedReport>(json)
            val response = CaptureService.allocateCapture(request)
            call.respondText(Json.encodeToString(response))
        }
        post("upload") {
            //println()
            //println("H: ${call.request.headers.toMap().entries.joinToString("\n")}")
            val json = call.receiveText()
            val request = Json.decodeFromString<WorkerCaptureUploadRequest>(json)
            val response = CaptureService.uploadImages(request)
            call.respondText(Json.encodeToString(response))
        }
        post("forceupload/{imageId}") {
            withContext(mainScope.coroutineContext) {
                val imageId = call.pathParameters["imageId"] ?: throw IllegalArgumentException()
                val sha1 = call.queryParameters["sha1"]?: throw IllegalArgumentException()
                println("FORCE UPLOAD!!!!!! $imageId")
                val imageBytes = call.receiveStream().use { ins->
                    val bytes = ByteArrayOutputStream()
                    var read = -1
                    while(ins.read().also { read = it } != -1) {
                        bytes.write(read)
                        if(bytes.size() % 5000 == 0) {
                            println("received: ${bytes.size()/1000}kb")
                        }
                    }
                    bytes.toByteArray()
                }
                println("force upload: received all image data")
                val response = CaptureService.forceUploadImage(UUID.fromString(imageId), sha1, imageBytes)
                call.respondText(Json.encodeToString(response))
            }

        }
        post("startupload") {
            val json = call.receiveText()
            val request = Json.decodeFromString<CaptureUploadStartRequest>(json)
            CaptureService.startUpload(request.channelId)
            call.respond(HttpStatusCode.OK)
        }
    }
}