package com.sq.demo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStringCommands.BitOperation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.sq.demo.utils.MyRedisUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringBootStudyRedisApplicationTests {

	@Autowired
	private MyRedisUtil redis;
	
	@Test
	public void set(){
		redis.set("key2", "springbootand password");
	}
	
	@Test
	public void get(){
		System.out.println(redis.get("key2"));
	}
	
	@Test
	public void del(){
		System.out.println(redis.del("key2"));
	}
	
	@Test
	public void bitCount(){
		Long bitCount = redis.bitCount("user");
		System.out.println("日活跃人数：" + bitCount);
	}
	
	@Test
	public void bitOp(){
		Long bitOp = redis.bitOp("testBitOp", BitOperation.OR, "user", "use2");
		System.out.println("bitOp返回值：" + bitOp);
	}
	
	@Test
	public void lock(){
		boolean lock = redis.lock("lockKey", "lockValue", 300L);
		System.out.println(lock);
	}
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testIndex() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/index"))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));
	}
	
	@Test
	public void testUnLock() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/unlock"))
			.andExpect(status().isOk())
			.andExpect(content().string("ok"));
	}
	
	@Test
	public void contextLoads() {
	}

}
