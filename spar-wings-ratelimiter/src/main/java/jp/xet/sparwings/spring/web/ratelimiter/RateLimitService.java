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

import javax.servlet.http.HttpServletRequest;

/**
 * TODO for daiuske
 * 
 * @since 0.8
 * @author daisuke
 */
public interface RateLimitService {
	
	/**
	 * 
	 * TODO for daisuke
	 * 
	 * @param request
	 * @param consumption
	 * @return {@link RateLimitDescriptor} or {@code null} if limitation is not applied.
	 * @since 0.8
	 */
	RateLimitDescriptor consume(HttpServletRequest request, long consumption);
	
	/**
	 * TODO for daisuke
	 * 
	 * @param limitationUnit
	 * @return
	 * @since 0.8
	 */
	RateLimitDescriptor get(HttpServletRequest request);
}
