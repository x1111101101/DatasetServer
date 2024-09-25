package io.github.x1111101101.dataset.capture.dto.instruction

import kotlinx.serialization.Serializable

/**
 * Server -> Manager
 */
@Serializable
data class CaptureStartResponse(val isSucceed: Boolean, val captureSessionId: String) {
}