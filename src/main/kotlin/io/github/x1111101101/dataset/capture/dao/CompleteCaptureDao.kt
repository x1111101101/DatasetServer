package io.github.x1111101101.dataset.capture.dao

import io.github.x1111101101.dataset.PROPERTIES
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val folder = File(PROPERTIES["COMPLETE_CAPTURE_STORAGE"].toString())
object CompleteCaptureDao {

    init {
        folder.mkdirs()
    }

    fun create(completeCapture: CompleteCapture) {
        val file = File(folder, "${completeCapture.uuid}.json")
        file.writeText(Json.encodeToString(completeCapture))
    }

}