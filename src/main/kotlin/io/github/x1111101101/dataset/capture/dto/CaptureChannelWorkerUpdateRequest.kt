package io.github.x1111101101.dataset.capture.dto

import kotlinx.serialization.Serializable

@Serializable
data class CaptureChannelWorkerUpdateRequest(
    val channelId: Int,
    val workers: List<Int>
)