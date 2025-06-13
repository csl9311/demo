package com.sample.backend.util.crypto.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sample.backend.util.crypto.CryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CryptoUtil_SHA implements CryptoUtil {

    private String encrypt(String text, String salt) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String textWithSalt = text + salt;
            byte[] hash = digest.digest(textWithSalt.getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            log.debug("hexString: {}", hexString.toString());
            return salt + hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        }
    }

    public String generateSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance("DRBG");
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            StringBuilder hexString = new StringBuilder();
            for (byte b : salt) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw new RuntimeException("DRBG Algorithm not found", e);
        }
    }

    @Override
    public String encrypt(String text, Map<String, Object> param) throws Exception {
        String salt = generateSalt();
        log.debug("salt: {}", salt);
        return encrypt(text, salt);
    }

    @Override
    public String decrypt(String text, Map<String, Object> param) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'decrypt'");
    }

    public boolean match(String encrypted, String matchText) throws Exception {
        String salt = encrypted.substring(0, 16);
        String hash = encrypted.substring(16);
        String tempHash = encrypt(matchText, salt).substring(16);

        log.debug("originhash: {}", hash);
        log.debug("temphash  : {}", tempHash);
        return tempHash.equals(hash);
    }
}
