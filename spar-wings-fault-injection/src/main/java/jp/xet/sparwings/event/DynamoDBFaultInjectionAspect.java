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
package jp.xet.sparwings.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect for fault injection to AWS DynamoDB cluent.
 * 
 * @since 0.18
 * @author daisuke
 */
@Aspect
public class DynamoDBFaultInjectionAspect extends AbstractFaultInjectionAspect {
	
	private static final Map<String, Supplier<RuntimeException>> SUPPLIERS;
	static {
		Map<String, Supplier<RuntimeException>> supplisers = new HashMap<>();
		supplisers.put("dynamodb:ProvisionedThroughputExceededException",
				() -> new ProvisionedThroughputExceededException("fault injected"));
		SUPPLIERS = Collections.unmodifiableMap(supplisers);
	}
	
	
	/**
	 * インスタンスを生成する。
	 */
	public DynamoDBFaultInjectionAspect() {
		super(SUPPLIERS);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Override
	@Around("execution(* com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.*(..))")
	public Object faultInjectionAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.faultInjectionAdvice(joinPoint);
	}
}
