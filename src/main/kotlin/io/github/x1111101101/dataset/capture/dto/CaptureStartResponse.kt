package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureStartResponse(val isSucceed: Boolean, val captureSessionId: String) {
}