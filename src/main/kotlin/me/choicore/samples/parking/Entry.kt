package me.choicore.samples.parking

import java.time.LocalDateTime

data class Entry(
    val parkingLot: Long,
    val destination: Destination,
    val licensePlate: LicensePlate,
    val enteredAt: LocalDateTime,
) : AbstractIdempotent(source = "$parkingLot:${destination.building}:${destination.unit}:${licensePlate.number}")
