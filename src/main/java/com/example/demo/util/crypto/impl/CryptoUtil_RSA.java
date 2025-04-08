package com.example.demo.util.crypto.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.example.demo.util.crypto.CryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CryptoUtil_RSA implements CryptoUtil {
    private static final KeyPair keyPair = generateRSAKeyPair();

    // RSA 키 쌍 생성 (2048비트)
    private static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            log.debug("generateRSAKeyPair success.");
            return keyPair;
        } catch (Exception e) {
            throw new RuntimeException("RSA 키 생성 실패", e);
        }
    }

    public String getBase64PublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getBase64PrivateKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /** PublicKey로 RSA 암호화 수행 */
    private String encrypt(String str, PublicKey publicKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytePlain = cipher.doFinal(str.getBytes());
            return Base64.getEncoder().encodeToString(bytePlain);
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

    /** PrivateKey RSA 복호화 수행 */
    private String decrypt(String str, PrivateKey privateKey) throws Exception {
        try {
            byte[] byteEncrypted = Base64.getDecoder().decode(str.getBytes());

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] bytePlain = cipher.doFinal(byteEncrypted);
            return new String(bytePlain, "utf-8");
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
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String encrypt(String text, Map<String, Object> options) throws Exception {
        try {
            if (StringUtils.isBlank(text)) {
                throw new Exception("text is required");
            }

            String base64PublicKey = MapUtils.getString(options, "publicKey");
            if (StringUtils.isBlank(base64PublicKey)) {
                throw new Exception("publicKey is required");
            }

            // Base64 => PublicKey
            byte[] bytePublicKey = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

            // 암호화
            return encrypt(text, publicKey);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        } catch (InvalidKeySpecException e) {
            log.error("InvalidKeySpecException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String decrypt(String text, Map<String, Object> options) throws Exception {
        try {
            if (StringUtils.isBlank(text)) {
                throw new Exception("text is required");
            }
    
            String base64PrivateKey = MapUtils.getString(options, "privateKey");
            if (StringUtils.isBlank(base64PrivateKey)) {
                throw new Exception("privateKey is required");
            }
            // base64 => PrivateKey
            byte[] decodedBase64PrivateKey = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedBase64PrivateKey);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

            // 복호화
            return decrypt(text, privateKey);
        } catch (InvalidKeySpecException e) {
            log.error("InvalidKeySpecException: {}", e.getMessage());
            throw e;
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            throw e;
        }
    }

}
