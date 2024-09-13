package io.github.x1111101101.dataset.capture.model.public

import io.github.x1111101101.dataset.formatted
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CompleteCapture(
    val snapshot: CaptureSnapshot,
    val captures: List<Capture>,
    val uuid: String = UUID.randomUUID().toString(),
    val createTime: String = Date(System.currentTimeMillis()).formatted()
) {
}