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

public class ImageRenderer {

    // Renders the math question into a Base64 encoded PNG string.
    public static String toBase64Png(String question) {
        // Define image properties
        int width = 200;
        int height = 50;

        // Create a blank image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Black text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.BOLD, 24));

        // Draw the question string (centered)
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(question)) / 2;
        int y = (height + fm.getAscent() - fm.getDescent()) / 2;
        
        g.drawString(question, x, y);
        g.dispose();

        // Encode to Base64 PNG
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", bos);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            // Log error and return null or throw exception
            return null; 
        }
    }
}
