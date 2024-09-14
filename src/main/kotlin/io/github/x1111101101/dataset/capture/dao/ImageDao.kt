package io.github.x1111101101.dataset.capture.dao

import io.github.x1111101101.dataset.PROPERTIES
import java.io.File
import java.util.UUID

object ImageDao {

    private val folder = File(PROPERTIES["IMAGE_STORAGE"].toString())

    init {
        folder.mkdirs()
    }

    fun create(uuid: UUID, data: ByteArray) {
        File(folder, "${uuid}.jpg").writeBytes(data)
        println("image saved")
    }

}