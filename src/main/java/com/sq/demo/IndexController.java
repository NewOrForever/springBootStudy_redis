package com.sq.demo;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sq.demo.PoJo.RedisLock;

/**
 * 用于jemeter测试redis分布式锁
 * 当然，这里没有构建分布式应用
 * 只是简单的模拟了一下redis锁有没有成功
 * 
 * <p>Description：</p>
 * @author： sq
 * @date： 2019年1月3日 下午1:03:47
 *
 */

@RestController
public class IndexController {
	
	@Autowired
	private RedisLock redisLock;
	
	@RequestMapping("/index")
	public String index(){
		String result = "争锁";
		if(redisLock.lock("lock", UUID.randomUUID().toString(), 300L)){
			result = "抢到锁";
			System.out.println("redis分布式锁");
		}
		System.out.println(result);
		return "OK";
	}
	
	@RequestMapping("/unlock")
	public String unLock(){
		redisLock.releaseLock("lock", "88040461-1a2c-40bc-8bb8-28b8e266fd3d");
		return "ok";
	}
	
	
	
}
