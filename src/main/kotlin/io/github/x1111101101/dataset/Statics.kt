package io.github.x1111101101.dataset

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