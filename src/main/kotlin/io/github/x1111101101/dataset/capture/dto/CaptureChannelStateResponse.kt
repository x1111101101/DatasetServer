package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureChannelStateResponse(
    val exist: Boolean,
    val captureSessionId: String,
    val workers: Map<Int, Boolean>,
) {
}