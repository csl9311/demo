package com.sample.backend.util.crypto;

import java.util.Map;

public interface CryptoUtil {
    public String encrypt(String text, Map<String, Object> param) throws Exception;

    public String decrypt(String text, Map<String, Object> param) throws Exception;
}
