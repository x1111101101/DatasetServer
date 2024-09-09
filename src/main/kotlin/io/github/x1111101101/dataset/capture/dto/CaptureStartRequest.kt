package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
class CaptureStartRequest(
    val channelId: Int,
    val devices: List<Int>,
    val scaleWeight: Double
) {
}