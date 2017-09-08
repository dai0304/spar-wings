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
package jp.xet.sparwings.common.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

/**
 * Servlet {@link Filter} implementation to generate Request-ID.
 * 
 * <p>Generated Request-ID is set to {@code X-Request-Id} response header
 * and {@link MDC} value which identified by {@code requestId}.</p>
 * 
 * @since 0.3
 * @author daisuke
 */
@Slf4j
public class RequestIdFilter extends OncePerRequestFilter {
	
	private static final String DEFAULT_REQUEST_ID_ATTRIBUTE = "requestId";
	
	private static final String DEFAULT_REQUEST_ID_HEADER = "Request-Id";
	
	private static final String DEFAULT_REQUEST_ID_MDC_KEY = "requestId";
	
	@NonNull
	@Getter
	@Setter
	private String requestIdAttribute = DEFAULT_REQUEST_ID_ATTRIBUTE;
	
	@NonNull
	@Getter
	@Setter
	private String requestIdMdcKey = DEFAULT_REQUEST_ID_MDC_KEY;
	
	@NonNull
	@Getter
	@Setter
	private String requestIdHeader = DEFAULT_REQUEST_ID_HEADER;
	
	@NonNull
	@Getter
	@Setter
	private RequestIdGenerator generator = new UuidRequestIdGenerator();
	
	
	@Override
	public void destroy() {
		// nothing to do
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.trace("Start issue request ID");
		String requestId = generator.generateRequestId(request);
		log.info("Request ID issued: {}", requestId);
		if (requestIdAttribute != null) {
			request.setAttribute(requestIdAttribute, requestId);
		}
		if (requestIdMdcKey != null) {
			MDC.put(requestIdMdcKey, requestId);
		}
		response.setHeader(requestIdHeader, requestId);
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(requestIdMdcKey);
		}
	}
}
