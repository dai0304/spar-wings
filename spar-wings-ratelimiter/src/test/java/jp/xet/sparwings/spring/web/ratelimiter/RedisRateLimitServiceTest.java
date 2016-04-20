/*
 * Copyright 2015-2016 the original author or authors.
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
import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

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
		sut.setRecoveryStrategy(req -> new RateLimitRecovery("user1", 2, 1000));
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
	public void consume100() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 100);
		// verify
		assertThat(actual.getMaxBudget(), is(1000L));
		assertThat(actual.getFillRate(), is(2L));
		assertThat(actual.getCurrentBudget(), is(900L));
	}
	
	@Test
	public void consume100_consume200() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 100);
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 200);
		// verify
		assertThat(actual.getMaxBudget(), is(1000L));
		assertThat(actual.getFillRate(), is(2L));
		assertThat(actual.getCurrentBudget(), is(700L));
	}
	
	@Test
	public void consume100_recover20_consume200() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 100);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(10000L))); // recover 20
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 200);
		// verify
		assertThat(actual.getMaxBudget(), is(1000L));
		assertThat(actual.getFillRate(), is(2L));
		assertThat(actual.getCurrentBudget(), is(720L));
	}
	
	@Test
	public void consume100_recover400_consume200() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 100);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(200000L))); // recover 400
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 200);
		// verify
		assertThat(actual.getMaxBudget(), is(1000L));
		assertThat(actual.getFillRate(), is(2L));
		assertThat(actual.getCurrentBudget(), is(800L));
	}
	
	@Test
	public void consume10_50threads() throws InterruptedException {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		int threadCount = 50;
		final CountDownLatch startLatch = new CountDownLatch(1);
		final CountDownLatch endLatch = new CountDownLatch(threadCount);
		ExecutorService ex = Executors.newFixedThreadPool(threadCount);
		long consume = 10;
		for (int i = 0; i < threadCount; i++) {
			ex.submit(() -> {
				try {
					startLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// exercise
				sut.consume(request, consume);
				endLatch.countDown();
			});
		}
		// exercise
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		startLatch.countDown();
		endLatch.await();
		RateLimitDescriptor actual = sut.get(request);
		// verify
		assertThat(actual.getMaxBudget(), is(1000L));
		assertThat(actual.getFillRate(), is(2L));
		assertThat(actual.getCurrentBudget(), is(500L));
	}
}
