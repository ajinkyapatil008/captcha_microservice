package com.example.captcha;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.captcha.dto.GenerateRequest;
import com.example.captcha.dto.ValidateRequest;
import com.example.captcha.model.CaptchaEntry;
import com.example.captcha.service.CaptchaService;
import com.example.captcha.service.store.InMemoryCaptchaStore;
import com.example.captcha.service.store.RedisCaptchaStore;

class CaptchaServiceTest {

    private RedisCaptchaStore redisStore;
    private InMemoryCaptchaStore memoryStore;
    private CaptchaService captchaService;

    @BeforeEach
    void setup() {
        // create mocks for the stores
        redisStore = mock(RedisCaptchaStore.class);
        memoryStore = mock(InMemoryCaptchaStore.class);

        // instantiate service with mocked stores
        captchaService = new CaptchaService(redisStore, memoryStore);

        // set TTL to a deterministic value
        ReflectionTestUtils.setField(captchaService, "ttlSeconds", 300L);
    }

    @Test
    void generate_shouldReturnIdAndQuestion_andStoreEntryInRedisWhenRedisAvailable() {
        // Arrange - nothing special; redisStore mock will accept put()

        // Act
        GenerateRequest req = new GenerateRequest();
        req.setAsImage(false);
        req.setDifficulty("L1");
        var resp = captchaService.generate(req);

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.getCaptchaId()).isNotBlank();
        assertThat(resp.getQuestion()).isNotBlank();
        assertThat(resp.getExpiresIn()).isEqualTo(300L);

        // verify redisStore.put called once with that id
        verify(redisStore, times(1)).put(eq(resp.getCaptchaId()), any(CaptchaEntry.class));
        // memoryStore should not be used in the successful redis path
        verifyNoInteractions(memoryStore);
    }

    @Test
    void validate_shouldReturnValidated_whenAnswerMatches() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String answer = "42";
        Instant expiresAt = Instant.now().plusSeconds(300);
        CaptchaEntry entry = new CaptchaEntry(answer, expiresAt);

        // mock memoryStore.get to return empty and redisStore.get to return our entry
        when(memoryStore.get(id)).thenReturn(Optional.empty());
        when(redisStore.get(id)).thenReturn(Optional.of(entry));

        // Act
        ValidateRequest req = new ValidateRequest();
        req.setCaptchaId(id);
        req.setAnswer(answer);

        var resp = captchaService.validate(req);

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("validated");

        // verify cleanup attempted on both stores
        verify(memoryStore).remove(id);
        verify(redisStore).remove(id);
    }

    @Test
    void validate_shouldReturnIncorrect_whenAnswerDoesNotMatch() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String storedAnswer = "10";
        Instant expiresAt = Instant.now().plusSeconds(300);
        CaptchaEntry entry = new CaptchaEntry(storedAnswer, expiresAt);

        when(memoryStore.get(id)).thenReturn(Optional.empty());
        when(redisStore.get(id)).thenReturn(Optional.of(entry));

        // Act
        ValidateRequest req = new ValidateRequest();
        req.setCaptchaId(id);
        req.setAnswer("wrong");

        var resp = captchaService.validate(req);

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isSuccess()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("incorrect");

        // ensure no deletion occurred on incorrect answer
        verify(redisStore, never()).remove(id);
        verify(memoryStore, never()).remove(id);
    }

    @Test
    void validate_shouldReturnExpired_whenEntryIsExpired() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String answer = "5";
        Instant expiresAt = Instant.now().minusSeconds(10); // expired
        CaptchaEntry entry = new CaptchaEntry(answer, expiresAt);

        when(memoryStore.get(id)).thenReturn(Optional.empty());
        when(redisStore.get(id)).thenReturn(Optional.of(entry));

        // Act
        ValidateRequest req = new ValidateRequest();
        req.setCaptchaId(id);
        req.setAnswer(answer);

        var resp = captchaService.validate(req);

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isSuccess()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("expired");

        // cleanup attempted
        verify(memoryStore).remove(id);
        verify(redisStore).remove(id);
    }
}

