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

/**
 * TODO for daisuke
 */
public class InMemoryRateLimitServiceTest {
	
	InMemoryRateLimitService sut;
	
	
	@Before
	public void setUp() {
		sut = new InMemoryRateLimitService();
		sut.setRecoveryStrategy(req -> new RateLimitRecovery("user1", 10, 1000000));
	}
	
	@After
	public void tearDown() throws Exception {
		Clock.setTimeSource(SystemClock.timeSource());
	}
	
	@Test
	public void consume1000() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 1000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(999000L));
	}
	
	@Test
	public void consume1000_consume2000() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 1000);
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(997000L));
	}
	
	@Test
	public void consume1000_50000recover500_consume2000() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(50000)));
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(997500L));
	}
	
	@Test
	public void consume1000_100000recover1000_consume2000() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(100000)));
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(998000L));
	}
	
	@Test
	public void consume1000_200000recover1000_consume2000() {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.EPOCH));
		sut.consume(request, 1000);
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(200000)));
		// exercise
		RateLimitDescriptor actual = sut.consume(request, 2000);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(998000L));
	}
	
	@Test
	public void consume100_100threads() throws InterruptedException {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
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
				sut.consume(request, consume);
				
				endLatch.countDown();
			});
		}
		long start = System.currentTimeMillis();
		// exercise
		startLatch.countDown(); // start
		endLatch.await();
		Clock.setTimeSource(new FixedTimeSource(TimePoint.from(start + (past * 1000))));
		RateLimitDescriptor actual = sut.get(request);
		// verify
		assertThat(actual.getMaxBudget(), is(1000000L));
		assertThat(actual.getFillRate(), is(10L));
		assertThat(actual.getCurrentBudget(), is(actual.getMaxBudget()
				- (consume * threadCount)
				+ (past * actual.getFillRate())));
	}
}
