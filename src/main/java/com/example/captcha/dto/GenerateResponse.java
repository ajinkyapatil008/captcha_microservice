package com.example.captcha.dto;

public class GenerateResponse {
    private String captchaId;
    private String question;
    private String imageBase64;
    private long expiresIn;

    public GenerateResponse() {}

    public GenerateResponse(String captchaId, String question, String imageBase64, long expiresIn) {
        this.captchaId = captchaId;
        this.question = question;
        this.imageBase64 = imageBase64;
        this.expiresIn = expiresIn;
    }

    public String getCaptchaId() { return captchaId; }
    public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
