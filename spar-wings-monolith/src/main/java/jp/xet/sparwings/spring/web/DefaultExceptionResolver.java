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
package jp.xet.sparwings.spring.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.xet.sparwings.aws.sns.NotificationService;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * Notify to developers when the HTTP request handling ends with Exception.
 * 
 * @since 0.3
 * @author daisuke
 */
public class DefaultExceptionResolver extends AbstractHandlerExceptionResolver {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultExceptionResolver.class);
	
	@Autowired
	NotificationService notificationService;
	
	@Setter
	private Collection<Class<? extends Throwable>> notificationIncludes = Arrays.asList(Throwable.class);
	
	@Setter
	private Collection<Class<? extends Throwable>> notificationExcludes = Collections.emptySet();
	
	
	@Override
	public ModelAndView doResolveException(HttpServletRequest req, HttpServletResponse res, Object handler, Exception e) {
		if (logger.isDebugEnabled()) {
			logger.debug("resolveException: {}", e.getClass().toString());
		} else if (logger.isTraceEnabled()) {
			logger.trace("resolveException: {}", req, e);
		}
		if (notificationExcludes.stream().anyMatch(c -> c.isAssignableFrom(e.getClass())) == false
				&& notificationIncludes.stream().anyMatch(c -> c.isAssignableFrom(e.getClass()))) {
			notificationService.notifyDev(e);
		}
		return null;
	}
}
