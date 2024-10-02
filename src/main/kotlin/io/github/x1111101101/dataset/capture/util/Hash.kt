package io.github.x1111101101.dataset.capture.util

import java.security.MessageDigest

fun ByteArray.toSha1String(): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val hashBytes = digest.digest(this)
    return hashBytes.joinToString("") { String.format("%02x", it) }
}