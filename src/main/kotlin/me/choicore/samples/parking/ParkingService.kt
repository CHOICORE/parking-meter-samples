package me.choicore.samples.parking

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ParkingService(
    private val idempotencyKeyResolver: IdempotencyKeyResolver,
) {
    fun enter(
        parkingLot: Long,
        destination: Destination,
        licensePlate: LicensePlate,
        enteredAt: LocalDateTime,
    ): Idempotent {
        val entryKey = "entered:$parkingLot:${destination.building}:${destination.unit}:${licensePlate.number}"
        return idempotencyKeyResolver.resolve(entryKey)
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
