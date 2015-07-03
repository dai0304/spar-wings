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
package jp.xet.sparwings.spring.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.xet.baseunits.time.Duration;
import jp.xet.baseunits.time.TimePoint;
import jp.xet.baseunits.time.TimeUnit;
import jp.xet.baseunits.timeutil.Clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * {@link HandlerInterceptor} implementation to logging request handling time.
 * 
 * @since #version#
 * @author daisuke
 */
public class ExecuteTimeInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(ExecuteTimeInterceptor.class);
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		TimePoint startTime = Clock.now();
		request.setAttribute("startTime", startTime);
		return true;
	}
	
	// after the handler is executed
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		TimePoint startTime = (TimePoint) request.getAttribute("startTime");
		TimePoint endTime = Clock.now();
		Duration executeTime = Duration.diff(endTime, startTime);
		
		// modified the exisitng modelAndView
		modelAndView.addObject("executeTime", executeTime);
		
		// log it
		if (logger.isDebugEnabled()) {
			logger.debug("[{}] executeTime : {}ms", handler, executeTime.to(TimeUnit.millisecond));
		}
	}
}
