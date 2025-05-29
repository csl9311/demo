package com.example.demo.aop;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private BigInteger logSeq = BigInteger.ZERO;
    private BigInteger processSeq = BigInteger.ZERO;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.example.demo.*.controller..*(..))")
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logSeq = logSeq.add(BigInteger.ONE);
        processSeq = BigInteger.ZERO;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        LocalDateTime ldt = LocalDateTime.now();

        HttpServletRequest request = attributes.getRequest();

        String method = request.getMethod();
        String uri    = request.getRequestURI();

        log.info("{}. [REQUEST ] {} {}", logSeq, uri, method);
        log.info("{}. [PARAMS  ] {}"   , logSeq, toJsonSafe(joinPoint.getArgs()));

        // Object response = joinPoint.proceed();
        Object result;
        try {
            result = joinPoint.proceed();  // 실제 메서드 실행
        } catch (Throwable throwable) {
            log.error("{}. [EXCEPTION] {} {}", logSeq, method, uri, throwable);
            throw throwable;
        }

        // 1. 수행시간
        Duration duration = Duration.between(ldt, LocalDateTime.now());
        String nanoSecond  = String.format("%09d", duration.toNanos()).substring(0, 3);
        log.info("{}. [DURATION] {}.{}", logSeq, duration.toSeconds(), nanoSecond);

        // RESPONSE 출력
        List<String> excludeList = Arrays.asList("/sample/downloadExcel");
        if (!excludeList.contains(uri)) {
            log.info("{}. [RESPONSE] {}", logSeq, toJsonSafe(result));
        } else {
            log.info("{}. [RESPONSE]", logSeq);
        }
        return result;
    }

    @Around("execution(* com.example.demo..*(..))")
    public Object allAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime ldt = LocalDateTime.now();
        Object response = joinPoint.proceed();

        Duration duration = Duration.between(ldt, LocalDateTime.now());
        String nanoSecond  = String.format("%09d", duration.toNanos()).substring(0, 3);

        String target = joinPoint.getTarget().toString();
               target = target.substring(0, target.indexOf("@"));
        String name = joinPoint.getSignature().getName();

        processSeq = processSeq.add(BigInteger.ONE);
        log.info("{}. {}. [CLASS   ] {}"   , logSeq, processSeq, target);
        log.info("{}. {}. [NAME    ] {}"   , logSeq, processSeq, name);
        log.info("{}. {}. [DURATION] {}.{}", logSeq, processSeq, duration.toSeconds(), nanoSecond);
        return response;
    }
    
    private Object toJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[unserializable]";
        }
    }
}
