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
package jp.xet.sparwings.faultinjection;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Abstract implementation injecting fault.
 * 
 * @since 0.18
 * @author daisuke
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractFaultInjectionAspect {
	
	/** {@code Fault-Injection} value - exception supplier map */
	private final Map<String, Supplier<RuntimeException>> suppliers;
	
	
	/**
	 * Do fault injection when the request header {@code Fault-Injection} which bound with this thread has specific value.
	 * 
	 * Override this method and annotate with advise annotation.
	 * 
	 * @param joinPoint {@link ProceedingJoinPoint}
	 * @return return value
	 * @throws Throwable -
	 * @since 0.18
	 */
	public Object faultInjectionAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		log.trace("Start faultInjectionAdvice");
		Optional<Exception> e = getFaultInjectonValue().map(suppliers::get).map(Supplier::get);
		if (e.isPresent()) {
			log.info("Fault injected!");
			throw e.get();
		}
		Object result = joinPoint.proceed();
		log.trace("End faultInjectionAdvice");
		return result;
	}
	
	protected Optional<String> getFaultInjectonValue() {
		try {
			RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
			if (requestAttributes instanceof ServletRequestAttributes) {
				ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
				HttpServletRequest request = servletRequestAttributes.getRequest();
				if (request != null) {
					String faultInjectonValue = request.getHeader("Fault-Injection");
					return Optional.ofNullable(faultInjectonValue);
				}
			}
		} catch (IllegalStateException e) { // No thread-bound request found
			// ignore
			log.trace(e.getMessage());
		}
		return Optional.empty();
	}
}
