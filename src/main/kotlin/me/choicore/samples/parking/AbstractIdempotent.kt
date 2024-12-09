package me.choicore.samples.parking

abstract class AbstractIdempotent(
    source: String,
) {
    val idempotencyKey: IdempotencyKey = IdempotencyKey(MessageDigesters.sha256(source, 32))
}
