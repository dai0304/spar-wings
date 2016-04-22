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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * TODO for daisuke
 * 
 * @since 0.8
 * @author daisuke
 */
@ToString
@Wither
@AllArgsConstructor
public class RateLimitDescriptor {
	
	@Getter
	private String limitationUnitName;
	
	/**
	 * Fill rate per millisec.
	 */
	@Getter
	private long fillRate;
	
	@Getter
	private long maxBudget;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private long currentBudget;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private long lastUpdateTime;
	
	
	public RateLimitDescriptor(String limitationUnitName, long fillRate, long maxBudget) {
		this.limitationUnitName = limitationUnitName;
		this.fillRate = fillRate;
		this.maxBudget = maxBudget;
	}
	
	public long computeWaitMillisecsToConsume(long cost) {
		return (cost - currentBudget) / fillRate;
	}
}
