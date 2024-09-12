package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureUploadRequest(
    val channelId: Int,
    val imageBase64: String,
    val captureSessionId: String,
    val deviceId: Int
) {
}