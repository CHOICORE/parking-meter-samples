package me.choicore.samples.parking

import java.util.UUID

@JvmInline
value class AccessKey private constructor(
    val value: String,
) {
    companion object {
        fun generate(): AccessKey =
            AccessKey(
                UUID
                    .randomUUID()
                    .toString()
                    .replace("-", ""),
            )

        fun of(value: String): AccessKey = AccessKey(value)

        val EMPTY = AccessKey("")
    }
}
