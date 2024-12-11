package me.choicore.samples.parking

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.types.Expiration
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ParkingService(
    val redisTemplate: RedisTemplate<String, String>,
    val objectMapper: ObjectMapper,
) {
    fun enter(entry: Entry) {
        if (entry.destination.isUnknown()) {
            entry.reject(accessKey = AccessKey.generate(), reason = "Unknown destination")
        }

        val idempotencyKey = createIdempotencyKey(entry.idempotencyKey)
        val accessKey = getAccessKey(idempotencyKey)
        when {
            accessKey != null -> onRecentEntryExists(accessKey = AccessKey.of(value = accessKey), entry = entry)
            else -> onRecentEntryNotExists(idempotencyKey, entry)
        }
    }

    private fun getAccessKey(idempotencyKey: String): String? = redisTemplate.opsForValue().get(idempotencyKey)

    private fun determineNextExecution(timestamp: LocalDateTime): Double =
        timestamp.plusSeconds(DUPLICATE_TIME_WINDOW_SECONDS).format(DATE_TIME_FORMATTER).toDouble()

    private fun createIdempotencyKey(idempotencyKey: IdempotencyKey): String = "$EVENTS_KEY:${idempotencyKey.value}"

    private fun getKey(accessKey: AccessKey) = "$ENTRIES_KEY:${accessKey.value}"

    private fun onRecentEntryExists(
        accessKey: AccessKey,
        entry: Entry,
    ) {
        redisTemplate.opsForValue().set(getKey(accessKey), getValue(entry))
    }

    private fun onRecentEntryNotExists(
        idempotencyKey: String,
        entry: Entry,
    ) {
        val accessKey = AccessKey.generate()
        val entryKey = getKey(accessKey)
        val entryValue = getValue(entry)
        redisTemplate.execute {
            it.multi()
            it.stringCommands().set(
                idempotencyKey.toByteArray(),
                accessKey.value.toByteArray(),
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
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        private const val PARKING_KEY_PREFIX = "parking"
        private const val ENTRIES_KEY = "entries"
        private const val EVENTS_KEY = "events:entered"
        private const val BATCH_KEY = "batch"
        private const val DUPLICATE_TIME_WINDOW_SECONDS: Long = 10
    }
}
