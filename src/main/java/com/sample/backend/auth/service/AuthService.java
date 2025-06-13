package com.sample.backend.auth.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.backend.util.crypto.impl.CryptoUtil_RSA;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private CryptoUtil_RSA rsaUtil;

    public String getPublicKey() throws Exception {
        // get public key
        return rsaUtil.getBase64PublicKey();
    }

    public JSONObject login(Map<String, Object> param) {
        log.debug("param: {}", param);
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
    
}
