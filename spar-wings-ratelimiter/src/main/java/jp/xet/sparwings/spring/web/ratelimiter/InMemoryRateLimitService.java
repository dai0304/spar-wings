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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import jp.xet.baseunits.timeutil.Clock;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RateLimitService} implementation to store values in memory.
 * 
 * @since 0.8
 * @author daisuke
 */
@Slf4j
public class InMemoryRateLimitService extends AbstractRateLimitService {
	
	private Map<String, RateLimitDescriptor> specs = new ConcurrentHashMap<>();
	
	
	@Override
	public synchronized RateLimitDescriptor consume(HttpServletRequest request, long consumption) {
		RateLimitDescriptor descriptor = computeRateLimitRecovery(request);
		if (descriptor == null) {
			return null;
		}
		descriptor = specs.computeIfAbsent(descriptor.getLimitationUnitName(), p -> computeRateLimitRecovery(request));
		if (descriptor == null) {
			return null;
		}
		long now = Clock.now().toEpochMillisec();
		
		long secSinceLastUpdate = now - descriptor.getLastUpdateTime();
		log.debug("Time (sec) since last update = {}", secSinceLastUpdate);
		long fill = secSinceLastUpdate * descriptor.getFillRate();
		long budget = Math.min(descriptor.getMaxBudget(), descriptor.getCurrentBudget() + fill);
		log.info("Budget before current request (filled {}): {}", fill, budget);
		
		budget -= consumption;
		log.info("Budget after current request (consumed {}): {}", consumption, budget);
		
		descriptor.setLastUpdateTime(now);
		descriptor.setCurrentBudget(budget);
		
		return descriptor;
	}
	
	@Override
	public RateLimitDescriptor get(HttpServletRequest request) {
		RateLimitDescriptor descriptor = computeRateLimitRecovery(request);
		if (descriptor == null) {
			return null;
		}
		descriptor = specs.computeIfAbsent(descriptor.getLimitationUnitName(), p -> computeRateLimitRecovery(request));
		if (descriptor == null) {
			return null;
		}
		long now = Clock.now().toEpochMillisec();
		
		long secSinceLastUpdate = now - descriptor.getLastUpdateTime();
		log.debug("Time (sec) since last update = {}", secSinceLastUpdate);
		long fill = secSinceLastUpdate * descriptor.getFillRate();
		long budget = Math.min(descriptor.getMaxBudget(), descriptor.getCurrentBudget() + fill);
		descriptor.setLastUpdateTime(now);
		
		descriptor.setCurrentBudget(budget);
		log.info("Current budget: (filled {}) and {}", fill, descriptor.getCurrentBudget());
		return descriptor;
	}
}
