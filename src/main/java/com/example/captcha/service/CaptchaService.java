package com.example.captcha.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.captcha.dto.GenerateRequest;
import com.example.captcha.dto.GenerateResponse;
import com.example.captcha.dto.ValidateRequest;
import com.example.captcha.dto.ValidateResponse;
import com.example.captcha.model.CaptchaEntry;
import com.example.captcha.service.store.InMemoryCaptchaStore;
import com.example.captcha.service.store.RedisCaptchaStore;
import com.example.captcha.util.ImageUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CaptchaService {

    private final CaptchaGenerator generator = new CaptchaGenerator();
    private final RedisCaptchaStore redisStore;
    private final InMemoryCaptchaStore memoryStore;

    @Value("${captcha.ttl:180}")
    private long ttlSeconds;

    public CaptchaService(RedisCaptchaStore redisStore, InMemoryCaptchaStore memoryStore) {
        this.redisStore = redisStore;
        this.memoryStore = memoryStore;
    }

    // public CaptchaGenerateResponse generateCaptcha(CaptchaGenerateRequest request) {
    //     // (Existing logic to determine difficulty)

    //     MathChallenge.Difficulty difficulty;
    //     try {
    //         difficulty = MathChallenge.Difficulty.valueOf(request.getDifficulty().toUpperCase());
    //     } catch (IllegalArgumentException e) {
    //         difficulty = MathChallenge.Difficulty.L2;
    //         log.warn("Invalid difficulty: {}. Defaulting to L2.", request.getDifficulty());
    //     }

    //     MathChallenge.Challenge challenge = MathChallenge.generate(difficulty);
    //     UUID captchaId = UUID.randomUUID();

    //     // Store the correct answer in the cache
    //     captchaCache.put(captchaId, challenge.answer());
    //     log.debug("Generated CAPTCHA ID: {} with Answer: {}", captchaId, challenge.answer());
        
    //     // --- ADD YOUR NEW CODE HERE ---
    //     String imageBase64 = null;
    //     if (request.isAsImage()) {
    //         // Call the renderer
    //         // NOTE: Ensure you have created the ImageRenderer class and its toBase64Png method.
    //         imageBase64 = com.captcha.util.ImageRenderer.toBase64Png(challenge.question());
    //     }
    //     // -----------------------------

    //     return CaptchaGenerateResponse.builder()
    //             .captchaId(captchaId)
    //             .question(challenge.question())
    //             .imageBase64(imageBase64) 
    //             .expiresIn(ttlSeconds)
    //             .build();
    // }

    public GenerateResponse generate(GenerateRequest req) {
        CaptchaGenerator.Generated g = generator.generate(req.getDifficulty());
        String id = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);
        CaptchaEntry entry = new CaptchaEntry(g.answer, expiresAt);

        // try to store in redis; if redis unavailable, fallback to memory
        try {
            redisStore.put(id, entry);
        } catch (Exception e) {
            memoryStore.put(id, entry);
        }

        String imageBase64 = null;
        if (req.isAsImage()) {
            imageBase64 = ImageUtil.textToImageBase64(g.question);
        }
        return new GenerateResponse(id, g.question, imageBase64, ttlSeconds);
    }

    public ValidateResponse validate(ValidateRequest req) {
        String id = req.getCaptchaId();
        String ans = req.getAnswer().trim();
        Optional<CaptchaEntry> found = Optional.empty();

        // check memory first (fast)
        try { found = memoryStore.get(id); } catch (Exception ignored) {}
        if (found.isEmpty()) {
            try { found = redisStore.get(id); } catch (Exception ignored) {}
        }
        if (found.isEmpty()) return new ValidateResponse(false, "expired or not found");
        CaptchaEntry e = found.get();
        if (e.getExpiresAt().isBefore(Instant.now())) {
            // expired
            try { memoryStore.remove(id); } catch (Exception ignored) {}
            try { redisStore.remove(id); } catch (Exception ignored) {}
            return new ValidateResponse(false, "expired");
        }
        if (e.getAnswer().equals(ans)) {
            // success: delete from stores
            try { memoryStore.remove(id); } catch (Exception ignored) {}
            try { redisStore.remove(id); } catch (Exception ignored) {}
            return new ValidateResponse(true, "validated");
        }
        return new ValidateResponse(false, "incorrect");
    }
}
