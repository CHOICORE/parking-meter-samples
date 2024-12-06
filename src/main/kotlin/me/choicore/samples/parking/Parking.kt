package me.choicore.samples.parking

import java.time.LocalDateTime

sealed interface Parking {
    val parkingLot: Long
    val licensePlate: String
    val direction: Direction
    val accessedAt: LocalDateTime
        get() =
            when (this) {
                is Entered -> this.enteredAt
                is Exited -> this.exitedAt
            }

    data class Entered(
        override val parkingLot: Long,
        override val licensePlate: String,
        val enteredAt: LocalDateTime,
    ) : Parking {
        override val direction: Direction = Direction.ENTRY
    }

    data class Exited(
        override val parkingLot: Long,
        override val licensePlate: String,
        val exitedAt: LocalDateTime,
    ) : Parking {
        override val direction: Direction = Direction.EXIT
    }

    enum class Direction {
        ENTRY,
        EXIT,
    }
}
