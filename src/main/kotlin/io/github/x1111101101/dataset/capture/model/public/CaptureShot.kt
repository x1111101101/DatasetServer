package io.github.x1111101101.dataset.capture.model.public

import kotlinx.serialization.Serializable

@Serializable
class CaptureShot(
    val id: String,
    val captures: List<Capture>,
    val foods: List<LabeledFood>,
    val scaleWeight: Double,
    val time: String,
) {

}