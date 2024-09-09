package io.github.x1111101101.dataset.capture.service

import io.github.x1111101101.dataset.capture.dto.CaptureChannelWorkerUpdateRequest
import io.github.x1111101101.dataset.capture.model.internal.CaptureChannel

object CaptureService {

    private val channels = Array(2) { CaptureChannel(it + 1) }.associateBy { it.id }

    suspend fun getLastSession(channelId: Int) {
        val channel = channels[channelId]

    }

    fun updateDeviceSetting(request: CaptureChannelWorkerUpdateRequest) {
        val channel = channels[request.channelId]
        if(channel == null) {

        }
    }

}