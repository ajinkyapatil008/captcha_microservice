package com.example.captcha.dto;

import jakarta.validation.constraints.NotBlank;

public class ValidateRequest {
    @NotBlank
    private String captchaId;
    @NotBlank
    private String answer;

    public String getCaptchaId() { return captchaId; }
    public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
