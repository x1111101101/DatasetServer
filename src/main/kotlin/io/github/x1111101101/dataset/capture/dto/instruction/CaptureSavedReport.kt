package io.github.x1111101101.dataset.capture.dto.instruction

import kotlinx.serialization.Serializable

/**
 * Worker -> Server
 */
@Serializable
data class CaptureSavedReport(
    val channelId: Int,
    val captureSessionId: String,
    val deviceId: Int
) {
}