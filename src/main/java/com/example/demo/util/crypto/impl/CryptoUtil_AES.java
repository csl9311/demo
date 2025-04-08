package com.example.demo.util.crypto.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.util.crypto.CryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CryptoUtil_AES implements CryptoUtil {

    private static final String ALGORITHM = "AES";

    @Value("${environment.AES_KEY}")
    private String SECRET_KEY; // 반드시 16, 24, 또는 32바이트여야 합니다.

    @Override
    public String encrypt(String text, Map<String, Object> param) throws Exception {
        try {
            log.debug("SECRET_KEY: {}", SECRET_KEY);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException: {}", e.getMessage());
            throw e;
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException: {}", e.getMessage());
            throw e;
        } catch (IllegalBlockSizeException e) {
            log.error("IllegalBlockSizeException: {}", e.getMessage());
            throw e;
        } catch (BadPaddingException e) {
            log.error("BadPaddingException: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String decrypt(String text, Map<String, Object> param) throws Exception {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encryptedBytes = Base64.getDecoder().decode(text);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException: {}", e.getMessage());
            throw e;
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException: {}", e.getMessage());
            throw e;
        } catch (IllegalBlockSizeException e) {
            log.error("IllegalBlockSizeException: {}", e.getMessage());
            throw e;
        } catch (BadPaddingException e) {
            log.error("BadPaddingException: {}", e.getMessage());
            throw e;
        }
    }

    public void generateKey() throws Exception {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    
            Files.createDirectories(Paths.get("secrets"));
            try (FileWriter writer = new FileWriter("secrets/aes.key")) {
                writer.write(encodedKey);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
            throw e;
        }
    }
}
