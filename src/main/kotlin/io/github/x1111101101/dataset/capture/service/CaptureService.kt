package io.github.x1111101101.dataset.capture.service

import io.github.x1111101101.dataset.capture.dto.CaptureChannelWorkerUpdateRequest
import io.github.x1111101101.dataset.capture.dto.CaptureStartRequest
import io.github.x1111101101.dataset.capture.dto.CaptureStartResponse
import io.github.x1111101101.dataset.capture.dto.CaptureStateResponse
import io.github.x1111101101.dataset.capture.model.internal.CaptureChannel
import io.github.x1111101101.dataset.capture.model.internal.CaptureJob
import kotlinx.coroutines.flow.*
import java.util.UUID

object CaptureService {

    private val channels = Array(2) { CaptureChannel(it + 1) }.associateBy { it.id }

    suspend fun getLastSession(channelId: Int) {
        val channel = channels[channelId]

    }

    fun startCaptureSession(request: CaptureStartRequest): CaptureStartResponse {
        val channelId = request.channelId
        val channel = channels[channelId] ?: return CaptureStartResponse(false, "")
        val sessionId = UUID.randomUUID()
        channel.startSession(sessionId, request.devices)
        return CaptureStartResponse(true, sessionId.toString())
    }

    suspend fun captureSessionState(channelId: Int): Flow<CaptureStateResponse> {
        val channel = channels[channelId] ?: return flowOf(CaptureStateResponse(false, "", emptyMap()))

        fun fromJob(job: CaptureJob?): CaptureStateResponse {
            if (job == null) return CaptureStateResponse(false, "", emptyMap())
            return CaptureStateResponse(
                true,
                job.sessionId.toString(),
                job.captures.map { it.workerId to it.imageId }.toMap()
            )
        }
        return flow {
            channel.currentJob.collect {
                emit(fromJob(it))
            }
        }
    }

    fun updateDeviceSetting(request: CaptureChannelWorkerUpdateRequest) {
        val channel = channels[request.channelId]
        if (channel == null) {

        }
    }

}