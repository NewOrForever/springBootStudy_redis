package com.sq.demo.service;

import org.springframework.stereotype.Component;

@Component
public class SecKill {
	
//	@Autowired
//	private final RedisLock redisLock;
//	public SecKill(RedisLock redisLock){
//		this.redisLock = redisLock;
//	}
	
	public int n = 500;
	
	
	
	public void secKill(){
		//synchronized ("锁") {
		//String lockValue = UUID.randomUUID().toString();
		//redisLock.lock("redislock", lockValue, 10);
		System.out.println(Thread.currentThread().getName() + "获得了锁");
		System.out.println(n--);
		//redisLock.releaseLock("redislock", lockValue);
		//}
	}
	
}
