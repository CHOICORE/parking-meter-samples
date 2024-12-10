package me.choicore.samples.parking

@JvmInline
value class LicensePlate private constructor(
    val number: String,
) {
    companion object {
        fun of(number: String): LicensePlate = LicensePlate(number = number)
    }
}
