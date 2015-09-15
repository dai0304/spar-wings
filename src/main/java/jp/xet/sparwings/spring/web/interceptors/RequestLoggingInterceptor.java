/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2015/06/04
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
package jp.xet.sparwings.spring.web.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * リクエストログを取得する {@link HandlerInterceptor} 実装クラス。
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class RequestLoggingInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (loggingRequired(request)) {
			logger.info("PreHandle request");
		}
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (loggingRequired(request) == false) {
			return;
		}
		if (ex == null) {
			logger.info("Complete request");
		} else {
			logger.error("Complete request(E)", ex);
		}
	}
	
	private boolean loggingRequired(HttpServletRequest request) {
		Map<String, String> map = MDC.getCopyOfContextMap();
		return map != null && map.size() != 0 && request.getRequestURI().equals("/health") == false;
	}
}
