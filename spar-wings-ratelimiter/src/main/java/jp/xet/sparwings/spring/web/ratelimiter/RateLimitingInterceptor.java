/*
 * Copyright 2015-2016 Miyamoto Daisuke.
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

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.xet.sparwings.spring.web.httpexceptions.HttpTooManyRequestsException;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
		int cost = computeCost(request, response, handler);
		String limitationUnit = getLimitationUnitName(request, response, handler);
		if (limitationUnit == null) {
			return; // through
		}
		RateLimitDescriptor desc = rateLimitService.consume(limitationUnit, cost);
		if (desc == null) {
			return; // through
		}
		
		if (responseHeader) {
			response.setHeader("X-RateLimit-Cost", String.valueOf(cost));
			response.setHeader("X-RateLimit-FillRate", String.valueOf(desc.getFillRate()));
			response.setHeader("X-RateLimit-MaximumBudget", String.valueOf(desc.getMaxBudget()));
			response.setHeader("X-RateLimit-CurrentBudget", String.valueOf(desc.getCurrentBudget()));
		}
		
		if (desc.getCurrentBudget() < 0) {
			long millisecsToWait = desc.computeWaitMillisecsToConsume(cost);
			if (responseHeader) {
				response.setHeader("X-RateLimit-RetryAfter", String.valueOf(millisecsToWait));
			}
			throw new HttpTooManyRequestsException(millisecsToWait);
		}
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	protected int computeCost(HttpServletRequest request, HttpServletResponse response, Object handler) {
		RateLimited rateLimited = null;
		if (handler instanceof HandlerMethod) {
			rateLimited = ((HandlerMethod) handler).getMethodAnnotation(RateLimited.class);
		}
		int cost = Optional.ofNullable(rateLimited).map(RateLimited::value).orElse(DEFAULT_CONSUMPTION);
		return cost;
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	protected String getLimitationUnitName(HttpServletRequest request, HttpServletResponse response, Object handler) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof String) {
				return (String) principal;
			} else if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				return userDetails.getUsername();
			}
			return Objects.toString(principal);
		}
		return null;
	}
}
