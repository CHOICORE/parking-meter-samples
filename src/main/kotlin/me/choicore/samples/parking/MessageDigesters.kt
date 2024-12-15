package me.choicore.samples.parking

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private const val HEX_DIGITS = "0123456789abcdef"

object MessageDigesters {
    private val messageDigest: MessageDigest by lazy { getInstance(algorithm = "SHA-256") }

    fun getInstance(algorithm: String): MessageDigest {
        try {
            return MessageDigest.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("Could not find MessageDigest with algorithm \"$algorithm\"", e)
        }
    }

    fun digest(input: String): ByteArray = messageDigest.digest(input.toByteArray(StandardCharsets.UTF_8))

    fun sha256(
        input: String,
        length: Int = 32,
    ): String {
        require(length in 1..32) { "Length must be between 1 and 32" }
        return buildString(length * 2) {
            digest(input)
                .take(length)
                .forEach { byte ->
                    append(HEX_DIGITS[byte.toInt() shr 4 and 0x0f])
                    append(HEX_DIGITS[byte.toInt() and 0x0f])
                }
        }
    }
}
