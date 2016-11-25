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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect for fault injection to Spring MVC controllers.
 * 
 * @since 0.18
 * @author daisuke
 */
@Slf4j
@Aspect
public class ControllerFaultInjectionAspect extends AbstractFaultInjectionAspect {
	
	private static final Map<String, Supplier<RuntimeException>> SUPPLIERS;
	static {
		Map<String, Supplier<RuntimeException>> supplisers = new HashMap<>();
		supplisers.put("controller:FaultInjectionException", () -> new FaultInjectionException("fault injected"));
		SUPPLIERS = Collections.unmodifiableMap(supplisers);
	}
	
	
	/**
	 * インスタンスを生成する。
	 */
	public ControllerFaultInjectionAspect() {
		super(SUPPLIERS);
	}
	
	@Pointcut("("
			+ "  within(@org.springframework.stereotype.Controller *)"
			+ "  || within(@org.springframework.web.bind.annotation.RestController *)"
			+ ")"
			+ " && @annotation(requestMapping)"
			+ " && !execution(* org.springframework.boot.autoconfigure.web.BasicErrorController.*(..))")
	public void controller(RequestMapping requestMapping) {
		// empty
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param joinPoint
	 * @param requestMapping
	 * @return
	 * @throws Throwable
	 */
	@Around("controller(requestMapping)")
	public Object faultInjectionAdvice(ProceedingJoinPoint joinPoint, RequestMapping requestMapping) throws Throwable {
		getFaultInjectonValue().filter(v -> v.startsWith("deferred:")).ifPresent(v -> {
			try {
				Integer sleep = Integer.valueOf(v.split(":", 2)[1]);
				Thread.sleep(sleep);
			} catch (Exception e) { // NOPMD
				log.warn("Unexpected exception", e);
			}
		});
		return super.faultInjectionAdvice(joinPoint);
	}
}
