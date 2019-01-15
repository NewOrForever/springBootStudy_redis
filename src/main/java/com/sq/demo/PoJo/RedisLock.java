package com.sq.demo.PoJo;

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
	
	private static final long TIME_OUT = 10000;  // 兜底超时时间，用于轮循锁，最大阻塞时间（ms）
	private static final long EXPIRE_TIME = 10; // key的过期时间（s）
	
	@Autowired
	private final MyRedisUtil redis;
	
	public RedisLock(MyRedisUtil redis){
		this.redis = redis;
	}
	
	
	/**
	 * 线程并发进入，争抢锁资源
	 * 
	 * @param lockKey
	 * @param lockValue
	 * @param expireTime
	 * @return
	 */
	public boolean lock(String lockKey, String lockValue, long expireTime){
		
		/**
		 * 1. 加锁，其他线程阻塞
		 * 2. 争锁失败，阻塞，轮循锁
		 */
		long currentTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - TIME_OUT < currentTime){
			boolean result = redis.setLock(lockKey, lockValue, expireTime);
			if(result){
				return true;
			}
		}
		
		return false;
	}
	
	
	public void releaseLock(String lockKey, String lockValue){
		String luaScript = "if redis.call('get',KEYS[1])==ARGV[1] "
				+ " then return redis.call('del',KEYS[1])"
				+ " else return 0"
				+ " end ";
		Boolean eval = redis.eval(luaScript, ReturnType.BOOLEAN, 1, lockKey, lockValue);
		System.out.println(eval);
	}
	
}
