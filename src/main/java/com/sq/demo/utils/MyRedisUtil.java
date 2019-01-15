package com.sq.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.BitOperation;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * springboot中使用redis
 * redis有5种数据类型：String, hash, list, set, zset
 * 	其中String, Hash主要是用于做缓存，存储的数据量不宜过大
 *	存储的是字符串数据，所以对象要存入缓存的时候需要先转成json
 *	获取数据的时候要讲json转成对象
 *  所以redis若是完全用于做缓存的话应该考虑使用springboot的cache redis，详见springbootDemo3
 * <p>Description：</p>
 * @author： sq
 * @date： 2018年12月5日 下午1:41:20
 *
 */

@Component
public class MyRedisUtil {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public void set(String key, String value){
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		if(this.stringRedisTemplate.hasKey(key)){
			System.out.println("key已存在");
		}
		opsForValue.set(key, value);
		System.out.println("key set success");
	}
	
	public String get(String key){
		return this.stringRedisTemplate.opsForValue().get(key);
	}
	
	public boolean del(String key){
		return this.stringRedisTemplate.delete(key);
	}
	
	
	/*********************springboot中操作redis的bitmap或者一些扩展操作可以使用如下的方式*******************/
	
	/**
	 * final修饰方法的参数
	 *	 对于基本类型：比如int，参数的值在方法内是不能改变的
	 *	 对于应用类型：参数所指向的引用是不能变的
	 *		1.String类型：值变的话，所指向的引用也就变了
	 *		2.User：user对象的值是可以修改的，但是user = new User()的话就不可以了
	 *
	 * @param key
	 * @return
	 */
	public Long bitCount(final String key){
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.bitCount(key.getBytes());
			}
			
		});
	}
	
	public Long bitOp(final String destKey, BitOperation bitOperation, final String... keys){
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Assert.hasText(destKey, "目标key必须有值");
				byte[][] byteKeys = byteKeys(keys);
				return connection.bitOp(bitOperation, destKey.getBytes(), byteKeys);
			}
		});
	}
	
	
	public byte[] byteKey(final String key) {
		Assert.hasText(key, "key必须有值");
		return key.getBytes();
	}
	
	public byte[][] byteKeys(final String... keys) {
		Assert.notNull(keys, "keys不能为null");
		
		byte[][] byteKeys = new byte[keys.length][]; 
		int i = 0;
		for (String key : keys) {
			byteKeys[i++] = byteKey(key);
		}
		return byteKeys;
	}
	
	/*****************************redis分布式锁（用于分布式应用同步访问共享资源）************************************/
	/**
	 * 这里只是写了一个加锁的底层实现，具体的实现在RedisLock类
	 * set key value [EX seconds] [PX milliseconds] [NX|XX]
	 * NX：set if not exist  - 排他性，独占锁
	 * EX：过期时间                     - 到时间自动释放锁，即使锁的持有者后续发生崩溃而没有解锁，锁也会因为到了过期时间而自动解锁（即key被删除），不会发生死锁
	 * @param lockKey      键
	 * @param lockValue	  线程id
	 * @return
	 */
	public boolean lock(String lockKey, String lockValue, Long expireTime){
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				// SET_IF_ABSENT:相当于NX
				// 使用了 set key value [EX seconds] [PX milliseconds] [NX|XX] 这个命令，保证了操作的原子性 
				return connection.set(byteKey(lockKey), byteKey(lockValue), Expiration.seconds(expireTime), SetOption.SET_IF_ABSENT);
			}
		});
		
	}
	
	public boolean setLock(String lockKey, String lockValue, Long expireTime){
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				// SET_IF_ABSENT:相当于NX
				// 使用了 set key value [EX seconds] [PX milliseconds] [NX|XX] 这个命令，保证了操作的原子性 
				return connection.set(byteKey(lockKey), byteKey(lockValue), Expiration.seconds(expireTime), SetOption.SET_IF_ABSENT);
			}
		});
		
	}
	
	// lua脚本
	public Boolean eval(String script, ReturnType returnType, int numKeys, String... keysAndArgs){
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				byte[][] byteKeysAndArgs = byteKeys(keysAndArgs);
				byte[] byteScript = byteKey(script);
				return connection.eval(byteScript, returnType, numKeys, byteKeysAndArgs);
			}
		});
	}
}
