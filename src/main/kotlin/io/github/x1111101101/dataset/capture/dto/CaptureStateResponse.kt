package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureStateResponse(
    val exist: Boolean,
    val captureSessionId: String,
    val photos: Map<Int, String>
) {
}