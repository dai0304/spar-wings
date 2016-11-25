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
package jp.xet.sparwings.aws.ec2;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

/**
 * A servlet filter that inserts various values retrieved from the incoming http request into the MDC.
 * 
 * <p>The values are removed after the request is processed.</p>
 *
 * @since 0.30
 * @author daisuke
 * @see ch.qos.logback.classic.helpers.MDCInsertingServletFilter
 */
public class ExtendedMDCInsertingServletFilter extends OncePerRequestFilter {
	
	static final String REQUEST_REMOTE_HOST_MDC_KEY = "remoteHost";
	
	static final String REQUEST_USER_AGENT_MDC_KEY = "userAgent";
	
	static final String REQUEST_REQUEST_URI = "requestURI";
	
	static final String REQUEST_QUERY_STRING = "queryString";
	
	static final String REQUEST_REQUEST_URL = "requestURL";
	
	static final String REQUEST_METHOD = "method";
	
	static final String REQUEST_X_FORWARDED_FOR = "xForwardedFor";
	
	@Setter
	@Getter
	private String prefix = "req_";
	
	
	@Override
	public void destroy() {
		// nothing to do
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			insertIntoMDC(request);
			filterChain.doFilter(request, response);
		} finally {
			clearMDC();
		}
	}
	
	void insertIntoMDC(ServletRequest request) {
		putIfNotNull(prefix + REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			putIfNotNull(prefix + REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
			StringBuffer requestURL = httpServletRequest.getRequestURL();
			if (requestURL != null) {
				putIfNotNull(prefix + REQUEST_REQUEST_URL, requestURL.toString());
			}
			putIfNotNull(prefix + REQUEST_METHOD, httpServletRequest.getMethod());
			putIfNotNull(prefix + REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
			putIfNotNull(prefix + REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader("User-Agent"));
			putIfNotNull(prefix + REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
		}
	}
	
	void clearMDC() {
		MDC.remove(prefix + REQUEST_REMOTE_HOST_MDC_KEY);
		MDC.remove(prefix + REQUEST_REQUEST_URI);
		MDC.remove(prefix + REQUEST_QUERY_STRING);
		// removing possibly inexistent item is OK
		MDC.remove(prefix + REQUEST_REQUEST_URL);
		MDC.remove(prefix + REQUEST_METHOD);
		MDC.remove(prefix + REQUEST_USER_AGENT_MDC_KEY);
		MDC.remove(prefix + REQUEST_X_FORWARDED_FOR);
	}
	
	private void putIfNotNull(String key, String value) {
		if (value != null && value.isEmpty() == false) {
			MDC.put(key, value);
		}
	}
}
