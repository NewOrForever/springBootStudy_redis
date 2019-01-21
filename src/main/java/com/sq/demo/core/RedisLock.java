package com.sq.demo.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.stereotype.Component;

import com.sq.demo.utils.MyRedisUtil;

/**
 * redis分布式锁实体对象
 * <p>Description：</p>
 * @author： sq
 * @date： 2019年1月3日 下午4:15:43
 *
 */

@Component
public class RedisLock {
	
	//private long TIME_OUT = 10000;  // 兜底超时时间，用于轮循锁，最大阻塞时间（ms）
	//private static final long EXPIRE_TIME = 10; // key的过期时间（s）
	private static final long DEFAULT_SLEEP_TIME = 10;
	
	@Autowired
	private final MyRedisUtil redis;
	public RedisLock(MyRedisUtil redis){
		this.redis = redis;
	}
	
	
	/**
	 * 阻塞所
	 * 没有兜底超时时间
	 * @param lockKey
	 * @param lockValue
	 * @param expireTime
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean lock(String lockKey, String lockValue, long expireTime) throws InterruptedException{
		
		/**
		 * 1. 加锁，其他线程阻塞
		 * 2. 争锁失败，阻塞，轮循锁
		 */
		while(true){
			if(setLockToRedis(lockKey, lockValue, expireTime)) return true;
		}
		
	}
	
	/**
	 * 阻塞锁
	 * 有兜底超时时间的lock
	 * @param lockKey
	 * @param lockValue
	 * @param expireTime  键过期时间
	 * @param timeOut	   键超时时间，当超时后不在轮循阻塞，直接返回false
	 * @return
	 * @throws InterruptedException
	 */
	public boolean lock(String lockKey, String lockValue, long expireTime, long timeOut) throws InterruptedException{
		while(timeOut >= 0){
			if(setLockToRedis(lockKey, lockValue, expireTime)) return true;
			// timeout
			timeOut -= DEFAULT_SLEEP_TIME;
		}
		return false;
	}
	
	/**
	 * 非阻塞锁
	 * @param lockKey
	 * @param lockValue
	 * @param expireTime
	 * @return
	 * @throws InterruptedException
	 */
	public boolean tryLock(String lockKey, String lockValue, long expireTime) throws InterruptedException{
		return setLockToRedis(lockKey, lockValue, expireTime);
	}
	
	public boolean setLockToRedis(String lockKey, String lockValue, long expireTime) throws InterruptedException{
		if(redis.setLock(lockKey, lockValue, expireTime)){
			return true;
		}
		Thread.sleep(DEFAULT_SLEEP_TIME);
		return false;
	}
	
	/**
	 * 释放自己的锁
	 * @param lockKey
	 * @param lockValue
	 * @return
	 */
	public boolean releaseLock(String lockKey, String lockValue){
		String luaScript = "if redis.call('get',KEYS[1])==ARGV[1] "
				+ " then return redis.call('del',KEYS[1])"
				+ " else return 0"
				+ " end ";
		return redis.eval(luaScript, ReturnType.BOOLEAN, 1, lockKey, lockValue);
		//System.out.println(eval);
	}
	
}
