package me.choicore.samples.parking

data class Destination(
    val building: String,
    val unit: String,
) {
    companion object {
        val UNKNOWN = Destination("-1", "-1")
    }
}
