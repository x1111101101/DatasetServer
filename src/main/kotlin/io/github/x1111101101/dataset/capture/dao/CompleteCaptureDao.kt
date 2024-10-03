package io.github.x1111101101.dataset.capture.dao

import io.github.x1111101101.dataset.PROPERTIES
import io.github.x1111101101.dataset.capture.model.public.CompleteCapture
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

private val folder = File(PROPERTIES["COMPLETE_CAPTURE_STORAGE"].toString())
object CompleteCaptureDao {

    init {
        folder.mkdirs()
    }

    fun create(completeCapture: CompleteCapture) {
        val file = File(folder, "${completeCapture.uuid}.json")
        file.writeText(Json.encodeToString(completeCapture))
    }

    fun getAll(): List<UUID> {
        val li = folder.listFiles()
        return li.filter { it.extension == "json" }
            .map { UUID.fromString(it.nameWithoutExtension) }
    }

    fun getSnapshot(id: UUID): String? {
        val file = File(folder, "${id}.json")
        if(!file.isFile) return null
        return file.readText()
    }

}