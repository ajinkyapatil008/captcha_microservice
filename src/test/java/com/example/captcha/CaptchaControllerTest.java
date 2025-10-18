package com.example.captcha;

// package com.example.captcha.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.captcha.controller.CaptchaController;
import com.example.captcha.dto.GenerateRequest;
import com.example.captcha.dto.GenerateResponse;
import com.example.captcha.dto.ValidateRequest;
import com.example.captcha.dto.ValidateResponse;
import com.example.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;

class CaptchaControllerTest {

    private MockMvc mockMvc;
    private CaptchaService captchaService;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        captchaService = mock(CaptchaService.class);
        CaptchaController controller = new CaptchaController(captchaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void generateEndpoint_shouldReturnGenerateResponse() throws Exception {
        GenerateResponse response = new GenerateResponse("id-123", "2 + 3", null, 180L);
        when(captchaService.generate(any(GenerateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/captcha/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asImage\":false, \"difficulty\":\"L1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.captchaId").value("id-123"))
                .andExpect(jsonPath("$.question").value("2 + 3"));

        verify(captchaService, times(1)).generate(any(GenerateRequest.class));
    }

    @Test
    void validateEndpoint_shouldReturnOkOnSuccess() throws Exception {
        ValidateResponse success = new ValidateResponse(true, "validated");
        when(captchaService.validate(any(ValidateRequest.class))).thenReturn(success);

        mockMvc.perform(post("/captcha/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"captchaId\":\"id-123\",\"answer\":\"5\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("validated"));

        verify(captchaService, times(1)).validate(any(ValidateRequest.class));
    }

    @Test
    void validateEndpoint_shouldReturnBadRequestOnFailure() throws Exception {
        ValidateResponse fail = new ValidateResponse(false, "incorrect");
        when(captchaService.validate(any(ValidateRequest.class))).thenReturn(fail);

        mockMvc.perform(post("/captcha/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"captchaId\":\"id-123\",\"answer\":\"7\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("incorrect"));

        verify(captchaService, times(1)).validate(any(ValidateRequest.class));
    }
}

