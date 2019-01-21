package com.sq.demo.core;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * redis lock 核心业务
 * <p>Description：</p>
 * @author： sq
 * @date： 2019年1月17日 下午2:45:35
 *
 */

@Component
@Aspect
public class RedisLockCore {
	
	@Autowired
	private final RedisLock redisLock;
	
	public RedisLockCore(RedisLock redisLock){
		this.redisLock = redisLock;
	}
	
	//execution(* com.sq.demo.controller.*.*(..))
	@Pointcut("execution(* com.sq.demo.service.*.secKill(..))")
	public void pointcut(){}
	
	// around -> before -> method -> around -> after
	@Around("pointcut()")
	public void around(ProceedingJoinPoint proceedingJoinPoint){
		// String lockValue = System.currentTimeMillis() + timeOut + "";
		String lockValue = UUID.randomUUID().toString();
		try {
			// 获取锁
			// lock锁失败后，业务逻辑是否执行？？
			if(redisLock.lock("redislock", lockValue, 10, 20000)){
				// 取锁成功
				proceedingJoinPoint.proceed();
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// 释放锁
			redisLock.releaseLock("redislock", lockValue);
		}
	}
}
