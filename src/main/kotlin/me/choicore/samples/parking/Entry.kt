package me.choicore.samples.parking

import java.time.LocalDateTime

data class Entry(
    val parkingLot: Long,
    val destination: Destination,
    val licensePlate: LicensePlate,
    val enteredAt: LocalDateTime,
) : AbstractIdempotent() {
    override fun getPlainText(): String = "$parkingLot:${destination.building}:${destination.unit}:${licensePlate.number}"

//    fun entered(idempotent: Idempotent): Entered = Entered(idempotent, parkingLot, destination, licensePlate, enteredAt)
}
