package com.example.captcha.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.captcha.dto.GenerateRequest;
import com.example.captcha.dto.GenerateResponse;
import com.example.captcha.dto.ValidateRequest;
import com.example.captcha.dto.ValidateResponse;
import com.example.captcha.service.CaptchaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(@RequestBody GenerateRequest request) {
        GenerateResponse resp = captchaService.generate(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@Valid @RequestBody ValidateRequest request) {
        ValidateResponse resp = captchaService.validate(request);
        if (!resp.isSuccess()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        return ResponseEntity.ok(resp);
    }
}
