package com.example.demo.auth.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.util.crypto.impl.CryptoUtil_AES;
import com.example.demo.util.crypto.impl.CryptoUtil_RSA;
import com.example.demo.util.crypto.impl.CryptoUtil_SHA;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private CryptoUtil_RSA rsaUtil;

    @Autowired
    private CryptoUtil_SHA shaUtil;

    @Autowired
    private CryptoUtil_AES aesUtil;

    public String getPublicKey() throws Exception {
        // TODO: 서비스 대상인지 확인하는 api 호출

        String originText = "test";
        
        String encSha = shaUtil.encrypt(originText, null);
        log.debug("[test] encSha: {}", encSha);
        log.debug("[test] match: {}", shaUtil.match(encSha, originText));

        String encAes = aesUtil.encrypt(originText, null);
        log.debug("[test] encAes: {}", encAes);
        log.debug("[test] decrypt: {}", aesUtil.decrypt(encAes, null));
        aesUtil.generateKey();
        // get public key
        return rsaUtil.getBase64PublicKey();
    }

    public JSONObject login(Map<String, Object> param) {
        log.debug("param: {}", param);
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
    
}
