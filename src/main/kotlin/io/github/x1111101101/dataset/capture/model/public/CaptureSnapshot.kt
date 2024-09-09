package io.github.x1111101101.dataset.capture.model.public

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class CaptureSnapshot(
    val scaleWeight: Double,
    val foods: Map<Int, LabeledFood>,
    val time: String = Date(System.currentTimeMillis()).formatted()
) {

    val totalWeight get() = foods.values.sumOf { it.weight }

    companion object {
        val EMPTY = CaptureSnapshot(
            scaleWeight = 0.0,
            foods = emptyMap(),
            time = Date(0).formatted()
        )
    }
}

private fun Date.formatted(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formattedTime: String = dateFormat.format(this)
    return formattedTime
}