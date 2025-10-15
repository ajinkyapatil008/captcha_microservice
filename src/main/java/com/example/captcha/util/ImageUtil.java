// package com.example.captcha.util;

// public class ImageUtil {

//     public static String textToImageBase64(String question) {
//         throw new UnsupportedOperationException("Not supported yet.");
//     }
    
// }

package com.example.captcha.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImageUtil {
    public static String textToImageBase64(String text) {
        try {
            int width = Math.max(180, text.length() * 14);
            int height = 60;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            int x = 10;
            int y = ((img.getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(text, x, y);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("image render failed", e);
        }
    }
}
