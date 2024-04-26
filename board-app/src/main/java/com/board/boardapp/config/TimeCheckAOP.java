package com.board.boardapp.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeCheckAOP {

    @Around("execution(* com.board.boardapp..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try{
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            if(timeMs > 1000)
                log.warn("{} --- time = {}ms", joinPoint.getSignature().toShortString(), timeMs);
            else
                log.info("{} --- time = {}ms", joinPoint.getSignature().toShortString(), timeMs);
        }
    }
}
