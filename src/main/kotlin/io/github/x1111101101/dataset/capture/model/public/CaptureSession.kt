package io.github.x1111101101.dataset.capture.model.public

import kotlinx.serialization.Serializable

@Serializable
data class CaptureSession(
    val id: String,
    val shots: List<CaptureShot>,
    val time: String
) {

}