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
package jp.xet.sparwings.spring.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

/**
 * ログイン中のユーザ名をMDCに出力するための{@link Filter ServletFilter} 実装クラス。
 * 
 * @since 0.3
 * @author daisuke
 */
public class UsernameLogFilter extends OncePerRequestFilter {
	
	private static final String USER_KEY = "username";
	
	
	@Override
	public void destroy() {
		// nothing to do
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		
		boolean successfulRegistration = false;
		if (auth != null) {
			String username = auth.getName();
			successfulRegistration = registerUsername(username);
		}
		
		try {
			filterChain.doFilter(request, response);
		} finally {
			if (successfulRegistration) {
				MDC.remove(USER_KEY);
			}
		}
	}
	
	/**
	 * Register the user in the {@link MDC} under {@link #USER_KEY}.
	 * 
	 * @param username the username
	 * @return true id the user can be successfully registered
	 */
	private boolean registerUsername(String username) {
		if (username != null && username.trim().isEmpty() == false) {
			MDC.put(USER_KEY, username);
			return true;
		}
		return false;
	}
}
