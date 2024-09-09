package io.github.x1111101101.dataset.capture.model.public

import kotlinx.serialization.Serializable

@Serializable
data class Capture(
    val workerId: Int,
    val imageId: String,
    val time: String
) {

}