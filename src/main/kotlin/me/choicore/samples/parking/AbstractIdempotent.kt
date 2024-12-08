package me.choicore.samples.parking

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

abstract class AbstractIdempotent {
    protected abstract fun getPlainText(): String

    fun getIdempotencyKey(): IdempotencyKey = IdempotencyKey(this.getPlainText().sha256(length = 16))

    private fun String.sha256(length: Int): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray(StandardCharsets.UTF_8))
            .take(length)
            .joinToString("") { "%02x".format(it) }
}
