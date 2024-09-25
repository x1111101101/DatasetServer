package io.github.x1111101101.dataset.capture.service

import io.github.x1111101101.dataset.capture.dao.CompleteCaptureDao
import io.github.x1111101101.dataset.capture.dao.ImageDao
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureStartResponse
import io.github.x1111101101.dataset.capture.dto.CaptureChannelStateResponse
import io.github.x1111101101.dataset.capture.dto.instruction.CaptureSavedReport
import io.github.x1111101101.dataset.capture.model.internal.CaptureChannel
import io.github.x1111101101.dataset.capture.model.internal.CaptureJob
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import io.github.x1111101101.dataset.mainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*

object CaptureService {

    private val channels = Array(2) { CaptureChannel(it + 1) }.associateBy { it.id }

    init {
        mainScope.launch {
            channels.values.forEach { channel ->
                launch {
                    channel.lastCompleteCapture.collect { completeCapture ->
                        saveCompleteCapture(completeCapture)
                    }
                }
            }
        }
    }

    fun startCaptureSession(request: CaptureStartRequest): CaptureStartResponse {
        val channelId = request.channelId
        val channel = channels[channelId] ?: return CaptureStartResponse(false, "")
        val sessionId = UUID.randomUUID()
        channel.startSession(sessionId, request.devices, request.snapshot)
        return CaptureStartResponse(true, sessionId.toString())
    }

    suspend fun uploadCapture(request: CaptureSavedReport, imageStream: InputStream) {
        println("upload request")
        val channelId = request.channelId
        val channel = channels[channelId] ?: throw IllegalArgumentException()
        val imageId = UUID.randomUUID()
        channel.addCapture(UUID.fromString(request.captureSessionId), request.deviceId, imageId)
        mainScope.launch {
            val bytes = imageStream.use { it.readBytes() }
            ImageDao.create(imageId, bytes)
        }.join()
    }

    fun captureSessionState(channelId: Int): CaptureChannelStateResponse {
        val channel = channels[channelId] ?: return CaptureChannelStateResponse(false, "", emptyMap())
        return fromJob(channel.currentJob.value)
    }

    suspend fun captureSessionStateAsFlow(channelId: Int): Flow<CaptureChannelStateResponse> {
        val channel = channels[channelId] ?: return flowOf(CaptureChannelStateResponse(false, "", emptyMap()))
        return channel.lastUpdate.map {
            fromJob(it?.job)
        }
    }

    private fun saveCompleteCapture(completeCapture: CompleteCapture) {
        println("SAVE COMPLETE: ${completeCapture}")
        CompleteCaptureDao.create(completeCapture)
    }

    private fun fromJob(job: CaptureJob?): CaptureChannelStateResponse {
        if (job == null) return CaptureChannelStateResponse(false, "", emptyMap())
        return CaptureChannelStateResponse(
            true,
            job.sessionId.toString(),
            job.captures.map { it.workerId to it.imageId }.toMap()
        )
    }

}