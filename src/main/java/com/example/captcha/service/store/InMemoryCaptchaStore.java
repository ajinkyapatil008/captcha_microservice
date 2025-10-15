package com.example.captcha.service.store;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.captcha.model.CaptchaEntry;

@Component
public class InMemoryCaptchaStore implements CaptchaStore {
    private final Map<String, CaptchaEntry> map = new ConcurrentHashMap<>();

    @Override
    public void put(String id, CaptchaEntry entry) {
        map.put(id, entry);
    }

    @Override
    public Optional<CaptchaEntry> get(String id) {
        CaptchaEntry e = map.get(id);
        if (e == null) return Optional.empty();
        if (e.getExpiresAt().isBefore(Instant.now())) {
            map.remove(id);
            return Optional.empty();
        }
        return Optional.of(e);
    }

    @Override
    public void remove(String id) {
        map.remove(id);
    }
}
