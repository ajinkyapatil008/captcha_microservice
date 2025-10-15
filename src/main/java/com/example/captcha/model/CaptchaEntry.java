package com.example.captcha.model;

import java.time.Instant;

public class CaptchaEntry {
    private String answer;
    private Instant expiresAt;

    public CaptchaEntry() {}
    public CaptchaEntry(String answer, Instant expiresAt) {
        this.answer = answer;
        this.expiresAt = expiresAt;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
