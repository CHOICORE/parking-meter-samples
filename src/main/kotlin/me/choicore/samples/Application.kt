package me.choicore.samples

import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.parking.Destination
import me.choicore.samples.parking.Entry
import me.choicore.samples.parking.LicensePlate
import me.choicore.samples.parking.ParkingService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.LocalDateTime

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val ac = runApplication<Application>(*args)
    val parkingService = ac.getBean(ParkingService::class.java)
    val enteredAt = LocalDateTime.now()

    val entry1 =
        Entry(
            parkingLot = 1,
            destination = Destination.UNKNOWN,
            licensePlate = LicensePlate.of(number = "123가1234"),
            enteredAt = enteredAt,
        )

    val entry2 = entry1.copy(enteredAt = enteredAt.plusSeconds(5))
    val entry3 = entry2.copy(enteredAt = enteredAt.plusSeconds(5))

    parkingService.enter(entry1)
    parkingService.enter(entry2)
    parkingService.enter(entry3)

    val redisTemplate = ac.getBean(StringRedisTemplate::class.java)
    val objectMapper = ac.getBean(ObjectMapper::class.java)
    val tasks =
        redisTemplate
            .opsForZSet()
            .reverseRangeByScore("batch", 0.0, Double.POSITIVE_INFINITY)
            ?.toList()

    if (tasks != null) {
        val entries =
            redisTemplate.opsForValue().multiGet(tasks)?.mapNotNull {
                println("retrieved: $it")
                objectMapper.readValue<Entry>(it, Entry::class.java)
            } ?: emptyList()

        for (entry in entries) {
            println(entry)
        }
    }
}
