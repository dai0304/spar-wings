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

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;

import jp.xet.baseunits.timeutil.Clock;

/**
 * {@link RateLimitService} implementation to store values in redis.
 * 
 * @since 0.8
 * @author daisuke
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRateLimitService extends AbstractRateLimitService {
	
	@Getter
	private final RedisTemplate<String, Long> redisTemplate;
	
	
	@Override
	public RateLimitDescriptor consume(HttpServletRequest request, long consumption) { // NOPMD - nc
		RateLimitDescriptor descriptor = computeRateLimitRecovery(request);
		if (descriptor == null) {
			return null;
		}
		String limitationUnitName = descriptor.getLimitationUnitName();
		long fillRate = descriptor.getFillRate();
		long maxBudget = descriptor.getMaxBudget();
		long now = Clock.now().toEpochMillisec();
		
		String tKey = "ratelimit:t:" + limitationUnitName;
		String cKey = "ratelimit:c:" + limitationUnitName;
		
		long delta = consumption;
		Long ts = redisTemplate.opsForValue().getAndSet(tKey, now);
		if (ts != null) {
			long secSinceLastUpdate = now - ts;
			log.debug("Time (sec) since last update = {}", secSinceLastUpdate);
			delta -= secSinceLastUpdate * fillRate;
		}
		Long carma = redisTemplate.opsForValue().increment(cKey, delta);
		if (carma == null) {
			throw new AssertionError("Unexpected condition. can not do in transaction");
		}
		
		if (carma < consumption) {
			redisTemplate.opsForValue().set(cKey, consumption);
			carma = consumption;
		}
		
		log.info("Budget before current request (filled {}): {}",
				ts == null ? "<unknown>" : (now - ts) * fillRate,
				ts == null ? maxBudget : maxBudget - carma + consumption);
		log.info("Budget after current request (consumed {}): {}", consumption, maxBudget - carma);
		
		long expire = carma / fillRate;
		redisTemplate.expire(tKey, expire, TimeUnit.SECONDS);
		redisTemplate.expire(cKey, expire, TimeUnit.SECONDS);
		
		descriptor.setCurrentBudget(maxBudget - carma);
		return descriptor;
	}
	
	@Override
	public RateLimitDescriptor get(HttpServletRequest request) {
		RateLimitDescriptor descriptor = computeRateLimitRecovery(request);
		if (descriptor == null) {
			return null;
		}
		String limitationUnitName = descriptor.getLimitationUnitName();
		long fillRate = descriptor.getFillRate();
		long maxBudget = descriptor.getMaxBudget();
		
		long now = Clock.now().toEpochMillisec();
		String tKey = "ratelimit:t:" + limitationUnitName;
		String cKey = "ratelimit:c:" + limitationUnitName;
		
		long delta = 0;
		Long ts = redisTemplate.opsForValue().getAndSet(tKey, now);
		if (ts != null) {
			long msSinceLastUpdate = now - ts;
			log.debug("Time (ms) since last update = {}", msSinceLastUpdate);
			delta -= msSinceLastUpdate * fillRate;
		}
		Long carma = redisTemplate.opsForValue().increment(cKey, delta);
		if (carma == null) {
			throw new AssertionError("Unexpected condition. can not do in transaction");
		}
		
		long expire = carma / fillRate;
		redisTemplate.expire(tKey, expire, TimeUnit.SECONDS);
		redisTemplate.expire(cKey, expire, TimeUnit.SECONDS);
		
		descriptor.setCurrentBudget(maxBudget - carma);
		log.info("Current budget: (filled {}) and {}", delta, descriptor.getCurrentBudget());
		return descriptor;
	}
}
