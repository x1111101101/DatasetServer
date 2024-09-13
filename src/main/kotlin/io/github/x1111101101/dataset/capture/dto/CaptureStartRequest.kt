package io.github.x1111101101.dataset.capture.dto

import io.github.x1111101101.dataset.capture.model.public.CaptureSnapshot
import kotlinx.serialization.Serializable

@Serializable
class CaptureStartRequest(
    val channelId: Int,
    val devices: List<Int>,
    val scaleWeight: Double,
    val snapshot: CaptureSnapshot
) {
}