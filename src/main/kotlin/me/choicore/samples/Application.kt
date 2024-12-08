package me.choicore.samples

import com.querydsl.jpa.impl.JPAQueryFactory
import me.choicore.samples.parking.AccessEntity
import me.choicore.samples.parking.AccessEntityRepository
import me.choicore.samples.parking.Destination
import me.choicore.samples.parking.Idempotent
import me.choicore.samples.parking.LicensePlate
import me.choicore.samples.parking.ParkingService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import java.time.LocalDateTime

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val ac = runApplication<Application>(*args)
    val queries = ac.getBean(JPAQueryFactory::class.java)
//    saveAccessEntry(ac)

    ac.getBean(ParkingService::class.java).enter(
        parkingLot = 1,
        destination = Destination.UNKNOWN,
        licensePlate = LicensePlate("123가1234"),
        enteredAt = LocalDateTime.now(),
    )

    ac.getBean(ParkingService::class.java).enter(
        parkingLot = 1,
        destination = Destination.UNKNOWN,
        licensePlate = LicensePlate("123가1234"),
        enteredAt = LocalDateTime.now().plusSeconds(5),
    )
}

private fun saveAccessEntry(ac: ConfigurableApplicationContext) {
    val repository = ac.getBean(AccessEntityRepository::class.java)

    val idempotent = Idempotent.generate()
    AccessEntity(
        licensePlate = LicensePlate("가1234"),
        parkingLot = 1,
        destination = Destination.UNKNOWN,
        enteredAt = LocalDateTime.now(),
        exitedAt = null,
        idempotent = idempotent,
    ).let(repository::save)
}
