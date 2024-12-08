package me.choicore.samples.parking

import java.time.LocalDateTime

data class Idempotent(
    val key: IdempotencyKey,
    val timestamp: LocalDateTime,
)
