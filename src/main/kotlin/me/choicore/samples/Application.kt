package me.choicore.samples

import com.querydsl.jpa.impl.JPAQueryFactory
import me.choicore.samples.parking.Destination
import me.choicore.samples.parking.Entry
import me.choicore.samples.parking.LicensePlate
import me.choicore.samples.parking.ParkingService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val ac = runApplication<Application>(*args)
    val queries = ac.getBean(JPAQueryFactory::class.java)
//    saveAccessEntry(ac)

    val enteredAt = LocalDateTime.of(2024, 12, 9, 19, 0, 0)
    val entry =
        Entry(
            parkingLot = 1,
            destination = Destination.UNKNOWN,
            licensePlate = LicensePlate("123가1234"),
            enteredAt = enteredAt,
        )

    val parkingService = ac.getBean(ParkingService::class.java)
    parkingService.enter(entry)
    val duplicate = entry.copy(enteredAt = enteredAt.plusSeconds(5))
    parkingService.enter(duplicate)
    parkingService.enter(duplicate.copy(destination = Destination("101", "101")))

    val redisTemplate = ac.getBean(StringRedisTemplate::class.java)
    val scan = redisTemplate.scan(ScanOptions.scanOptions().match("parking:entries:*").build())
    for (s in scan) {
        // 찾은 키의 가장 최신 값
        val latestEntry =
            redisTemplate
                .opsForZSet()
                .reverseRangeByScore(s, 0.0, enteredAt.toEpochSecond(ZoneOffset.UTC).toDouble(), 0, 1)
                ?.firstOrNull()
        println(latestEntry)
    }

//    ac.getBean(ParkingService::class.java).enter(
//        parkingLot = 1,
//        destination = Destination.UNKNOWN,
//        licensePlate = LicensePlate("123가1234"),
//        enteredAt = LocalDateTime.now(),
//    )
//
//    ac.getBean(ParkingService::class.java).enter(
//        parkingLot = 1,
//        destination = Destination.UNKNOWN,
//        licensePlate = LicensePlate("123가1234"),
//        enteredAt = LocalDateTime.now().plusSeconds(5),
//    )
}

// private fun saveAccessEntry(ac: ConfigurableApplicationContext) {
//    val repository = ac.getBean(AccessEntityRepository::class.java)
//
//    val idempotent = Idempotent.generate()
//    AccessEntity(
//        licensePlate = LicensePlate("가1234"),
//        parkingLot = 1,
//        destination = Destination.UNKNOWN,
//        enteredAt = LocalDateTime.now(),
//        exitedAt = null,
//        idempotent = idempotent,
//    ).let(repository::save)
// }
