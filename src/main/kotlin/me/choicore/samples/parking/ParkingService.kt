package me.choicore.samples.parking

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class ParkingService(
    val cache: StringRedisTemplate,
    val objectMapper: ObjectMapper,
) {
    fun enter(entry: Entry) {
        println(entry)
        val key = this.determineKey(entry)
        val existing: String? = cache.opsForValue().get(key)
        val idempotencyKey =
            if (existing != null) {
                val score = LocalDateTime.of(2024, 12, 9, 19, 0, 0).toEpochSecond(ZoneOffset.UTC).toDouble()
                // 새로운 value를 기존 score로 저장
                val newValue = objectMapper.writeValueAsString(entry) // 새로운 value
                val entryKey = "parking:entries:${entry.getIdempotencyKey().value}"
                cache.opsForZSet().add(entryKey, newValue, score)

                IdempotencyKey(existing)
            } else {
                val idempotencyKey = entry.getIdempotencyKey()
                cache.opsForValue().set(key, idempotencyKey.value, 10, java.util.concurrent.TimeUnit.SECONDS)
                val enteredAt = entry.enteredAt
                val entryKey = "parking:entries:${idempotencyKey.value}"
                val value = objectMapper.writeValueAsString(entry)
                cache.opsForZSet().add(entryKey, value, enteredAt.toEpochSecond(ZoneOffset.UTC).toDouble())
                cache.expire(entryKey, 10, java.util.concurrent.TimeUnit.SECONDS)
                idempotencyKey
            }
    }

// Key Structure
//    parking:entries -> Sorted Set {
//        "parking:entries:b7209945d4a9d02e98afd3fb56de24cc" -> score: 1733770800
//    }
// 실제 데이터는
//    parking:entries:b7209945d4a9d02e98afd3fb56de24cc -> String (실제 JSON 데이터)

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
