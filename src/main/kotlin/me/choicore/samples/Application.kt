package me.choicore.samples

import com.fasterxml.jackson.databind.ObjectMapper
import me.choicore.samples.parking.Destination
import me.choicore.samples.parking.Entry
import me.choicore.samples.parking.LicensePlate
import me.choicore.samples.parking.ParkingService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDateTime

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val ac = runApplication<Application>(*args)
    val redisTemplate = ac.getBean(StringRedisTemplate::class.java)
    val enteredAt = LocalDateTime.of(2024, 12, 9, 19, 0, 0)
    val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()
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

    val tasks =
        redisTemplate
            .opsForZSet()
            .reverseRangeByScore(
                "batch",
                0.0,
                Double.POSITIVE_INFINITY,
            )

    if (tasks != null) {
        val multiGet =
            redisTemplate.opsForValue().multiGet(tasks)?.mapNotNull {
                objectMapper.readValue<Entry>(it, Entry::class.java)
            } ?: emptyList()

        println(multiGet)
    }
}
