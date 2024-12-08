package me.choicore.samples.parking

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ParkingService(
    val cache: StringRedisTemplate,
) {
    fun enter(entry: Entry) {
        val key = this.determineKey(entry)
        val existing: String? = cache.opsForValue().get(key)
        val idempotencyKey =
            if (existing != null) {
                IdempotencyKey(existing)
            } else {
                entry.getIdempotencyKey()
            }
    }

    private fun determineKey(entry: Entry) = "parking:entered:${entry.getIdempotencyKey()}"

    fun enter(
        parkingLot: Long,
        destination: Destination,
        licensePlate: LicensePlate,
        enteredAt: LocalDateTime,
    ) {
        // 요청이 중복되는지 확인
    }

    fun exit(
        parkingLot: Long,
        licensePlate: LicensePlate,
        enteredAt: LocalDateTime,
        exitedAt: LocalDateTime,
        idempotent: Idempotent?,
    ) {
    }
}

// @Service
// class ParkingService(
//    private val idempotencyKeyResolver: IdempotencyKeyResolver,
// ) {
//    fun enter(entry: Entry) {
//        val idempotent: Idempotent = resolveIdempotencyKey(entry)
// //        return entry.entered(idempotent)
//        TODO()
//    }
//
//    fun exit(
//        parkingLot: Long,
//        licensePlate: LicensePlate,
//        enteredAt: LocalDateTime,
//        exitedAt: LocalDateTime,
//        idempotent: Idempotent?,
//    ) {
//    }
//
//    private fun resolveIdempotencyKey(entry: Entry) = idempotencyKeyResolver.resolve(entry.key)
// }
