package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureUploadRequest(
    val channelId: Int,
    val captureSessionId: String,
    val deviceId: Int
) {
}