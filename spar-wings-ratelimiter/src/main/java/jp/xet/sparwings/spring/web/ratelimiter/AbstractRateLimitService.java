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

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

/**
 * TODO for daisuke
 * 
 * @since 0.17
 * @author daisuke
 */
public abstract class AbstractRateLimitService implements RateLimitService {
	
	private long fillRate = 10L;
	
	private long maxBudget = 1000000L;
	
	@Setter
	private Function<HttpServletRequest, RateLimitRecovery> recoveryStrategy =
			req -> new RateLimitRecovery(req.getRemoteAddr(), fillRate, maxBudget);
	
	
	protected RateLimitRecovery computeRateLimitRecovery(HttpServletRequest request) {
		return recoveryStrategy.apply(request);
	}
}