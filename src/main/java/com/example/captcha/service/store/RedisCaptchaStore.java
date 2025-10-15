package com.example.captcha.service.store;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.example.captcha.model.CaptchaEntry;

@Component
public class RedisCaptchaStore implements CaptchaStore {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCaptchaStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String id, CaptchaEntry entry) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        long ttl = Math.max(5, Duration.between(Instant.now(), entry.getExpiresAt()).toSeconds());
        ops.set(id, entry, Duration.ofSeconds(ttl));
    }

    @Override
    public Optional<CaptchaEntry> get(String id) {
        Object o = redisTemplate.opsForValue().get(id);
        if (o instanceof CaptchaEntry ce) return Optional.of(ce);
        // Jackson-serialized object may become LinkedHashMap when deserialized; try to map manually:
        if (o instanceof java.util.Map) {
            try {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> m = (java.util.Map<String, Object>) o;
                String answer = (String) m.get("answer");
                Object ex = m.get("expiresAt");
                Instant expiresAt = null;
                if (ex instanceof String string) {
                    expiresAt = Instant.parse(string);
                }
                if (answer != null && expiresAt != null) {
                    return Optional.of(new CaptchaEntry(answer, expiresAt));
                }
            } catch (Exception ignored) { }
        }
        return Optional.empty();
    }

    @Override
    public void remove(String id) {
        redisTemplate.delete(id);
    }
}
