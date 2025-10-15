package com.example.captcha.service;

import java.util.Random;

public class CaptchaGenerator {
    private final Random random = new Random();

    public static class Generated {
        public final String question;
        public final String answer;

        public Generated(String q, String a) { question = q; answer = a; }
    }

    public Generated generate(String difficulty) {
        return switch (difficulty == null ? "L1" : difficulty) {
            case "L2" -> genTwoDigit();
            case "L3" -> genAlgebra();
            default -> genSingleDigit();
        };
    }

    private Generated genSingleDigit() {
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int op = random.nextInt(3);
        if (op == 0) return new Generated(a + " + " + b, String.valueOf(a + b));
        if (op == 1) return new Generated(a + " - " + b, String.valueOf(a - b));
        return new Generated(a + " x " + b, String.valueOf(a * b));
    }

    private Generated genTwoDigit() {
        int a = 10 + random.nextInt(90);
        int b = 10 + random.nextInt(90);
        int op = random.nextInt(3);
        if (op == 0) return new Generated(a + " + " + b, String.valueOf(a + b));
        if (op == 1) return new Generated(a + " - " + b, String.valueOf(a - b));
        return new Generated(a + " x " + b, String.valueOf(a * b));
    }

    private Generated genAlgebra() {
        // simple linear eq: a*x + b = c
        int a = 1 + random.nextInt(5);
        int x = 1 + random.nextInt(9);
        int b = random.nextInt(10);
        int c = a * x + b;
        String q = a + "x + " + b + " = " + c + "; find x";
        return new Generated(q, String.valueOf(x));
    }
}
