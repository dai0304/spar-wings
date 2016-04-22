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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.xet.sparwings.spring.web.httpexceptions.HttpTooManyRequestsException;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * リクエスト毎にレートリミットを判断する {@link HandlerInterceptor} 実装クラス。
 * 
 * @since 0.8
 * @author daisuke
 */
public class RateLimitingInterceptor extends HandlerInterceptorAdapter {
	
	private static final int DEFAULT_CONSUMPTION = 100;
	
	private final RateLimitService rateLimitService;
	
	@Getter
	@Setter
	private boolean responseHeader = true;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param rateLimitService {@link RateLimitService}
	 * @since 0.8
	 */
	public RateLimitingInterceptor(RateLimitService rateLimitService) {
		this.rateLimitService = rateLimitService;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (rateLimitService != null && isRateLimitTarget(request, response, handler)) {
			rateLimit(request, response, handler);
		}
		return super.preHandle(request, response, handler);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	protected boolean isRateLimitTarget(HttpServletRequest request, HttpServletResponse response, Object handler) {
		return true;
	}
	
	private void rateLimit(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws HttpTooManyRequestsException {
		int cost = computeCost(request, handler);
		RateLimitDescriptor desc = rateLimitService.consume(request, cost);
		if (desc == null) {
			return; // through
		}
		
		if (responseHeader) {
			response.setHeader("RateLimit-Unit", desc.getLimitationUnitName());
			response.setHeader("RateLimit-Cost", String.valueOf(cost));
			response.setHeader("RateLimit-CurrentBudget", String.valueOf(desc.getCurrentBudget()));
			response.setHeader("RateLimit-MaximumBudget", String.valueOf(desc.getMaxBudget()));
			response.setHeader("RateLimit-FillRate", String.valueOf(desc.getFillRate()));
		}
		
		if (desc.getCurrentBudget() < 0) {
			long millisecsToWait = desc.computeWaitMillisecsToConsume(cost);
			if (responseHeader) {
				long secsToWait = Math.floorDiv(millisecsToWait, 1000L);
				response.setHeader("Retry-After", String.valueOf(secsToWait));
			}
			throw new HttpTooManyRequestsException(millisecsToWait);
		}
	}
	
	/**
	 * Compute cost of request.
	 * 
	 * @param request The request
	 * @param handler The handler of request
	 * @return cost
	 */
	protected int computeCost(HttpServletRequest request, Object handler) {
		RateLimited rateLimited = null;
		if (handler instanceof HandlerMethod) {
			rateLimited = ((HandlerMethod) handler).getMethodAnnotation(RateLimited.class);
		}
		int cost = Optional.ofNullable(rateLimited).map(RateLimited::value).orElse(DEFAULT_CONSUMPTION);
		return cost;
	}
}
