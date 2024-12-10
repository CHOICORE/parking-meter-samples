package me.choicore.samples.parking

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.configuration.jpa.AutoIncrement
import java.time.LocalDateTime

@Entity
@Table(name = "access_event")
class AccessEntity(
    val accessKey: AccessKey,
    val parkingLot: Long,
    destination: Destination,
    val licensePlate: LicensePlate,
    val enteredAt: LocalDateTime,
    var exitedAt: LocalDateTime? = null,
) : AutoIncrement() {
    val building: String = destination.building
    val unit: String = destination.unit
}
