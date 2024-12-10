package me.choicore.samples.parking

@JvmInline
value class IdempotencyKey private constructor(
    val value: String,
) {
    companion object {
        fun of(value: String): IdempotencyKey = IdempotencyKey(value)
    }
}
