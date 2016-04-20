/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/04/20
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.xet.sparwings.spring.web.ratelimiter;

import java.util.Objects;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author daisuke
 */
public abstract class AbstractRateLimitService implements RateLimitService {
	
	@Setter
	private Function<HttpServletRequest, RateLimitRecovery> recoveryStrategy = req -> {
		String limitationUnitName = null;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof String) {
				limitationUnitName = (String) principal;
			} else if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				limitationUnitName = userDetails.getUsername();
			} else {
				limitationUnitName = Objects.toString(principal);
			}
		}
		
		return new RateLimitRecovery(limitationUnitName, 10, 1000000);
	};
	
	
	protected RateLimitRecovery computeRateLimitRecovery(HttpServletRequest request) {
		return recoveryStrategy.apply(request);
	}
}
