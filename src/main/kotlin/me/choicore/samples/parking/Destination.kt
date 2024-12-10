package me.choicore.samples.parking

data class Destination(
    val building: String,
    val unit: String,
) {
    fun isUnknown(): Boolean = this == UNKNOWN

    companion object {
        val UNKNOWN = Destination("-1", "-1")
    }
}
