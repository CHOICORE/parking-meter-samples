package me.choicore.samples.parking

abstract class Idempotence(
    source: String,
) {
    val idempotencyKey: IdempotencyKey = IdempotencyKey.of(value = MessageDigesters.sha256(input = source, length = 16))
}
