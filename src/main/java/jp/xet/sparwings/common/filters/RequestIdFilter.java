/*
 * Copyright 2015 Miyamoto Daisuke, Inc.
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
package jp.xet.sparwings.common.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet {@link Filter} implementation to generate Request-ID.
 * 
 * <p>Generated Request-ID is set to {@code X-Request-Id} response header
 * and {@link MDC} value which identified by {@code requestId}.</p>
 * 
 * @since #version#
 * @author daisuke
 */
public class RequestIdFilter extends OncePerRequestFilter {
	
	private static final String HEADER_REQUEST_ID_KEY = "X-Request-Id";
	
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	
	
	@Override
	public void destroy() {
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestId = generateRequestID();
		MDC.put(MDC_REQUEST_ID_KEY, requestId);
		response.setHeader(HEADER_REQUEST_ID_KEY, requestId);
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_REQUEST_ID_KEY);
		}
	}
	
	/**
	 * Generate request ID.
	 * 
	 * @return request ID
	 * @since #version#
	 */
	protected String generateRequestID() {
		return UUID.randomUUID().toString();
	}
}
