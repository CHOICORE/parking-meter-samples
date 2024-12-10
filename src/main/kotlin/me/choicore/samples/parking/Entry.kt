package me.choicore.samples.parking

import java.time.LocalDateTime

data class Entry(
    val parkingLot: Long,
    val destination: Destination,
    val licensePlate: LicensePlate,
    val enteredAt: LocalDateTime,
) : Idempotence(source = "$parkingLot:${destination.building}:${destination.unit}:${licensePlate.number}") {
    fun reject(
        accessKey: AccessKey,
        reason: String,
    ) = Parking.Entered(
        accessKey = accessKey,
        parkingLot = parkingLot,
        destination = destination,
        licensePlate = licensePlate,
        enteredAt = enteredAt,
        reason = reason,
    )
}
