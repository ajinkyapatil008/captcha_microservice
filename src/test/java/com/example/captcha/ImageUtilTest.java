package com.example.captcha;

// package com.example.captcha.util;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.example.captcha.util.ImageUtil;

class ImageUtilTest {

    @Test
    void textToImageBase64_shouldReturnValidPngBase64() {
        String base64 = ImageUtil.textToImageBase64("3 + 4");
        assertThat(base64).isNotNull().isNotBlank();

        byte[] bytes = Base64.getDecoder().decode(base64);
        assertThat(bytes.length).isGreaterThan(8);

        // PNG signature bytes: 89 50 4E 47 0D 0A 1A 0A
        assertThat(bytes[0]).isEqualTo((byte) 0x89);
        assertThat(bytes[1]).isEqualTo((byte) 0x50); // 'P'
        assertThat(bytes[2]).isEqualTo((byte) 0x4E); // 'N'
        assertThat(bytes[3]).isEqualTo((byte) 0x47); // 'G'
    }
}
