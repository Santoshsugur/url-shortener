package com.santosh.URLShortnerApplication.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        return generateCode(CODE_LENGTH);
    }

    public String generate() {
        return generateCode(CODE_LENGTH);
    }

    public String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(BASE62.length());
            sb.append(BASE62.charAt(randomIndex));
        }
        return sb.toString();
    }
}
