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

import jp.xet.baseunits.timeutil.Clock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * {@link RateLimitService} implementation to store values in redis.
 * 
 * @since 0.8
 * @author daisuke
 */
@RequiredArgsConstructor
public class RedisRateLimitService extends AbstractRateLimitService {
	
	private static Logger logger = LoggerFactory.getLogger(RedisRateLimitService.class);
	
	@Getter
	private final RedisTemplate<String, Long> redisTemplate;
	
	
	@Override
	public RateLimitDescriptor consume(HttpServletRequest request, long consumption) {
		RateLimitRecovery recovery = computeRateLimitRecovery(request);
		if (recovery == null) {
			return null;
		}
		String limitationUnitName = recovery.getLimitationUnitName();
		long fillRate = recovery.getFillRate();
		long maxBudget = recovery.getMaxBudget();
		
		long now = Clock.now().toEpochSec();
		String tKey = "ratelimit:t:" + limitationUnitName;
		String cKey = "ratelimit:c:" + limitationUnitName;
		
		long delta = consumption;
		Long ts = redisTemplate.opsForValue().getAndSet(tKey, now);
		if (ts != null) {
			long secSinceLastUpdate = now - ts;
			logger.debug("Time (sec) since last update = {}", secSinceLastUpdate);
			delta -= secSinceLastUpdate * fillRate;
		}
		Long carma = redisTemplate.opsForValue().increment(cKey, delta);
		
		if (carma < consumption) {
			redisTemplate.opsForValue().set(cKey, consumption);
			carma = consumption;
		}
		logger.debug("Current budget and consumption: {} - {}", maxBudget - carma + consumption, consumption);
		
		long expire = carma / fillRate;
		redisTemplate.expire(tKey, expire, TimeUnit.SECONDS);
		redisTemplate.expire(cKey, expire, TimeUnit.SECONDS);
		
		return new RateLimitDescriptor(limitationUnitName, fillRate, maxBudget, maxBudget - carma);
	}
	
	@Override
	public RateLimitDescriptor get(HttpServletRequest request) {
		RateLimitRecovery recovery = computeRateLimitRecovery(request);
		if (recovery == null) {
			return null;
		}
		String limitationUnitName = recovery.getLimitationUnitName();
		long fillRate = recovery.getFillRate();
		long maxBudget = recovery.getMaxBudget();
		
		long now = Clock.now().toEpochSec();
		String tKey = "ratelimit:t:" + limitationUnitName;
		String cKey = "ratelimit:c:" + limitationUnitName;
		
		long delta = 0;
		Long ts = redisTemplate.opsForValue().getAndSet(tKey, now);
		if (ts != null) {
			long msSinceLastUpdate = now - ts;
			logger.debug("Time (ms) since last update = {}", msSinceLastUpdate);
			delta -= msSinceLastUpdate * fillRate;
		}
		Long carma = redisTemplate.opsForValue().increment(cKey, delta);
		
		long expire = carma / fillRate;
		redisTemplate.expire(tKey, expire, TimeUnit.SECONDS);
		redisTemplate.expire(cKey, expire, TimeUnit.SECONDS);
		
		return new RateLimitDescriptor(limitationUnitName, fillRate, maxBudget, maxBudget - carma);
	}
}
