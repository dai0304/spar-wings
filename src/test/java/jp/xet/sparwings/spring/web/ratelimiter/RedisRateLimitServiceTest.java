/*
 * Copyright 2015 Miyamoto Daisuke.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.xet.sparwings.spring.web.ratelimiter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.xet.baseunits.time.TimePoint;
import jp.xet.baseunits.timeutil.Clock;
import jp.xet.baseunits.timeutil.FixedTimeSource;
import jp.xet.baseunits.timeutil.SystemClock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * TODO for daisuke
 */
public class RedisRateLimitServiceTest {
	
	RedisRateLimitService sut;
	
	
	@Before
	public void setUp() throws Exception {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName("localhost");
		jedisConnectionFactory.setPort(6379);
		jedisConnectionFactory.setDatabase(5);
		jedisConnectionFactory.afterPropertiesSet();
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		
		sut = new RedisRateLimitService(redisTemplate);
	}
	
	@After
	public void tearDown() throws Exception {
		Clock.setTimeSource(SystemClock.timeSource());
		sut.getRedisTemplate().execute((RedisCallback<Boolean>) connection -> {
			connection.flushDb();
			return true;
		});
	}
	
	@Test
	public void consume1000() {
		// exercise
		RateLimitDescriptor actual = sut.consume("user1", 1000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(999000L));
	}
	
	@Test
	public void consume1000_consume2000() {
		// setup
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume("user1", 1000);
		// exercise
		RateLimitDescriptor actual = sut.consume("user1", 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(997000L));
	}
	
	@Test
	public void consume1000_50000recover500_consume2000() {
		// setup
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume("user1", 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(50000)));
		// exercise
		RateLimitDescriptor actual = sut.consume("user1", 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(997500L));
	}
	
	@Test
	public void consume1000_100000recover1000_consume2000() {
		// setup
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume("user1", 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(100000)));
		// exercise
		RateLimitDescriptor actual = sut.consume("user1", 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(998000L));
	}
	
	@Test
	public void consume1000_200000recover1000_consume2000() {
		// setup
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume("user1", 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(200000)));
		// exercise
		RateLimitDescriptor actual = sut.consume("user1", 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(998000L));
	}
	
	@Test
	public void consume100_100threads() throws InterruptedException {
		int threadCount = 100;
		final CountDownLatch startLatch = new CountDownLatch(1);
		final CountDownLatch endLatch = new CountDownLatch(threadCount);
		ExecutorService ex = Executors.newFixedThreadPool(threadCount);
		long past = 10;
		long consume = 100;
		for (int i = 0; i < threadCount; i++) {
			ex.submit(() -> {
				try {
					startLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// exercise
				sut.consume("user1", consume);
				endLatch.countDown();
			});
		}
		long start = System.currentTimeMillis();
		// exercise
		startLatch.countDown(); // start
		endLatch.await();
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(start + (past * 1000))));
		RateLimitDescriptor actual = sut.get("user1");
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(actual.getMaxBudget()
				- (consume * threadCount)
				+ (past * actual.getFillRate())));
	}
	
	@Test
	public void consume100_100threads_2() throws InterruptedException {
		int threadCount = 10000;
		final CountDownLatch endLatch = new CountDownLatch(threadCount);
		ExecutorService ex = Executors.newFixedThreadPool(100);
		long past = 10;
		long consume = 100;
		long start = System.currentTimeMillis();
		// exercise
		for (int i = 0; i < threadCount; i++) {
			ex.submit(() -> {
				// exercise
				sut.consume("user1", consume);
				endLatch.countDown();
			});
		}
		endLatch.await();
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(start + (past * 1000))));
		RateLimitDescriptor actual = sut.get("user1");
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(actual.getMaxBudget()
				- (consume * threadCount)
				+ (past * actual.getFillRate())));
	}
}
