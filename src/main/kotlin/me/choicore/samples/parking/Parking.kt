package me.choicore.samples.parking

import java.time.LocalDateTime

sealed interface Parking {
    data class Entered(
        val accessKey: AccessKey,
        val parkingLot: Long,
        val destination: Destination,
        val licensePlate: LicensePlate,
        val enteredAt: LocalDateTime,
        val reason: String?,
    )
}
