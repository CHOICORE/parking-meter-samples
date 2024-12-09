package me.choicore.samples.parking

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.types.Expiration
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DUPLICATE_TIME_WINDOW_SECONDS: Long = 10

@Service
class ParkingService(
    val cache: StringRedisTemplate,
    val objectMapper: ObjectMapper,
) {
    fun enter(entry: Entry) {
        val idempotencyKey = createIdempotencyKey(entry.idempotencyKey.value)
        val accessKey = getAccessKey(idempotencyKey)
        when {
            accessKey != null -> onExists(accessKey, entry)
            else -> onNotExists(idempotencyKey, entry)
        }
    }

    private fun getAccessKey(idempotencyKey: String): String? = cache.opsForValue().get(idempotencyKey)

    private fun determineNextExecution(timestamp: LocalDateTime): Double =
        timestamp.plusSeconds(DUPLICATE_TIME_WINDOW_SECONDS).format(DATE_TIME_FORMATTER).toDouble()

    private fun createIdempotencyKey(value: String) = "$EVENTS_KEY:$value"

    private fun getKey(accessKey: String) = "$ENTRIES_KEY:$accessKey"

    private fun onExists(
        accessKey: String,
        entry: Entry,
    ) {
        val key = getKey(accessKey)
        val value = getValue(entry)
        cache.opsForValue().set(key, value)
    }

    private fun onNotExists(
        idempotencyKey: String,
        entry: Entry,
    ) {
        val accessKey = AccessKey.generate().value
        val entryKey = getKey(accessKey)
        val entryValue = getValue(entry)

        cache.execute {
            it.multi()
            it.stringCommands().set(
                idempotencyKey.toByteArray(),
                accessKey.toByteArray(),
                Expiration.seconds(DUPLICATE_TIME_WINDOW_SECONDS),
                RedisStringCommands.SetOption.UPSERT,
            )

            it.stringCommands().set(
                entryKey.toByteArray(),
                entryValue.toByteArray(),
            )

            it.zSetCommands().zAdd(
                BATCH_KEY.toByteArray(),
                determineNextExecution(entry.enteredAt),
                entryKey.toByteArray(),
            )

            it.exec()
        }
    }

    private fun getValue(entry: Entry): String = objectMapper.writeValueAsString(entry)

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        private const val PARKING_KEY_PREFIX = "parking"
        private const val ENTRIES_KEY = "$PARKING_KEY_PREFIX:entries"
        private const val EVENTS_KEY = "$PARKING_KEY_PREFIX:events:entered"
        private const val BATCH_KEY = "batch"
    }
}
