package com.example.captcha.dto;

public class GenerateRequest {
    private boolean asImage = false;
    private String difficulty = "L1"; // L1, L2, L3

    public boolean isAsImage() { return asImage; }
    public void setAsImage(boolean asImage) { this.asImage = asImage; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
