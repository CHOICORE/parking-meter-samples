package me.choicore.samples.parking

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class IdempotencyKeyResolver(
    private val redisTemplate: StringRedisTemplate,
) {
    fun resolve(key: String): Idempotent {
        val existing: String? = redisTemplate.opsForValue().get(key)
        return if (existing != null) {
            Idempotent(existing)
        } else {
            val idempotent: Idempotent = Idempotent.generate()
            redisTemplate
                .opsForValue()
                .set(key, idempotent.key, DUPLICATE_WINDOW_SECONDS, TimeUnit.SECONDS)
            idempotent
        }
    }

    companion object {
        private const val DUPLICATE_WINDOW_SECONDS = 10L
    }
}
