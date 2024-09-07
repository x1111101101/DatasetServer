package io.github.x1111101101.dataset.capture.model.public

import kotlinx.serialization.Serializable

@Serializable
data class LabeledFood(
    val foodName: String,
    val foodWeight: Double
) {

}