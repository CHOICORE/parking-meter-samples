package me.choicore.samples.parking

import java.util.UUID

@JvmInline
value class Idempotent(
    val key: String,
) {
    companion object {
        fun generate(): Idempotent = Idempotent(UUID.randomUUID().toString().replace("-", ""))
    }
}
