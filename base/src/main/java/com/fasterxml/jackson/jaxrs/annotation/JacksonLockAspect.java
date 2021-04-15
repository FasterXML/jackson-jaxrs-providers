package com.fasterxml.jackson.jaxrs.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.jaxrs.annotation.JacksonLocks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronized AOP
 * Creator	victoryang00
 * Time	4.15
 */
@Component
@Scope
@Aspect
@Order(1)
public class JacksonLockAspect {
    /**
     * service is singleton by default, and there is only one instance of lock under concurrency.
     */
    private static  Lock lock = new ReentrantLock(true);//Mutual exclusion lock Parameter default false, not fair lock

    //Service layer Pointcut for error logging
    @Pointcut("@annotation(com.fasterxml.jackson.jaxrs.annotation.JacksonLocks)")
    public void JacksonLockAspect() {

    }

    @Around("JacksonLocks()")
    public  Object around(ProceedingJoinPoint joinPoint) {
        lock.lock();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally{
            lock.unlock();
        }
        return obj;
    }
}