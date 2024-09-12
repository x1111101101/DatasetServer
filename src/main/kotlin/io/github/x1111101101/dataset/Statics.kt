package io.github.x1111101101.dataset

import java.text.SimpleDateFormat
import java.util.*

val PROPERTIES = Properties().apply {
    loadProperties(this)
}

private fun loadProperties(instance: Properties) {
    val url = ClassLoader.getSystemClassLoader().getResource("private.properties") ?: throw IllegalStateException("missing private.properties file")
    url.openStream().use {
        it.bufferedReader().use { reader-> instance.load(reader) }
    }
}

inline fun Date.formatted(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formattedTime: String = dateFormat.format(this)
    return formattedTime
}