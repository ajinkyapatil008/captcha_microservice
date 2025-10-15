package com.example.captcha.service.store;

import java.util.Optional;

import com.example.captcha.model.CaptchaEntry;

public interface CaptchaStore {
    void put(String id, CaptchaEntry entry);
    Optional<CaptchaEntry> get(String id);
    void remove(String id);
}
