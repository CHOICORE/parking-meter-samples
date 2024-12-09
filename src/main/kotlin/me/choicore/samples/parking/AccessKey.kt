package me.choicore.samples.parking

import java.util.UUID

@JvmInline
value class AccessKey(
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
    }
}
