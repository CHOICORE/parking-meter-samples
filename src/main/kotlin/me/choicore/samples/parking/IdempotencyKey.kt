package me.choicore.samples.parking

@JvmInline
value class IdempotencyKey(
    val value: String,
)
