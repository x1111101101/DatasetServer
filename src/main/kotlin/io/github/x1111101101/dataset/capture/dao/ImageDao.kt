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
        folder.mkdirs()
        File(folder, "${uuid}.jpg").writeBytes(data)
        println("image saved: $uuid")
    }

    fun get(id: UUID): ByteArray? {
        val folders = listOf(folder)
        folders.forEach {
            val file = File(it, "${id}.jpg")
            if(file.isFile) return file.readBytes()
        }
        return null
    }

}