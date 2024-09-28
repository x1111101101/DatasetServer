package io.github.x1111101101.dataset.capture.dto.instruction

import kotlinx.serialization.Serializable

@Serializable
data class WorkerCaptureUploadResponse(
    val succeed: List<String>
)