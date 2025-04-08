package com.example.demo.auth.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @GetMapping("get/key/public")
    public ResponseEntity<String> getPublicKey() throws Exception {
        try {
            return ResponseEntity.ok(service.getPublicKey());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("login")
    public ResponseEntity<JSONObject> login(@RequestBody Map<String, Object> param) throws Exception {
        try {
            return ResponseEntity.ok(service.login(param));
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}