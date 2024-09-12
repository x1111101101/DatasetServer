package io.github.x1111101101.dataset.capture.service

import io.github.x1111101101.dataset.capture.dao.ImageDao
import io.github.x1111101101.dataset.capture.dto.*
import io.github.x1111101101.dataset.capture.model.internal.CaptureChannel
import io.github.x1111101101.dataset.capture.model.internal.CaptureJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Base64
import java.util.UUID

object CaptureService {

    private val channels = Array(2) { CaptureChannel(it + 1) }.associateBy { it.id }

    fun startCaptureSession(request: CaptureStartRequest): CaptureStartResponse {
        val channelId = request.channelId
        val channel = channels[channelId] ?: return CaptureStartResponse(false, "")
        val sessionId = UUID.randomUUID()
        channel.startSession(sessionId, request.devices)
        return CaptureStartResponse(true, sessionId.toString())
    }

    suspend fun uploadCapture(request: CaptureUploadRequest) {
        val channelId = request.channelId
        val channel = channels[channelId] ?: throw IllegalArgumentException()
        val imageId = UUID.randomUUID()
        channel.addCapture(UUID.fromString(request.captureSessionId), request.deviceId, imageId)
        coroutineScope {
            launch {
                val data = Base64.getDecoder().decode(request.imageBase64)
                ImageDao.create(imageId, data)
            }
        }
    }

    fun captureSessionState(channelId: Int): CaptureStateResponse {
        val channel = channels[channelId] ?: return CaptureStateResponse(false, "", emptyMap())
        return fromJob(channel.currentJob.value)
    }

    suspend fun captureSessionStateAsFlow(channelId: Int): Flow<CaptureStateResponse> {
        val channel = channels[channelId] ?: return flowOf(CaptureStateResponse(false, "", emptyMap()))


        return flow {
            channel.lastUpdate.collect {
                it?.job?.let { emit(fromJob(it)) }
            }
        }
    }

    private fun fromJob(job: CaptureJob?): CaptureStateResponse {
        if (job == null) return CaptureStateResponse(false, "", emptyMap())
        return CaptureStateResponse(
            true,
            job.sessionId.toString(),
            job.captures.map { it.workerId to it.imageId }.toMap()
        )
    }

}