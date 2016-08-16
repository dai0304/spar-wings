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
package jp.xet.sparwings.common.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for application initialization.
 * 
 * @since 0.3
 * @author daisuke
 */
public final class InitializationUtil {
	
	private static Logger log = LoggerFactory.getLogger(InitializationUtil.class);
	
	static final String MASK_STRING = "********";
	
	
	/**
	 * Log all system properties.
	 * 
	 * <p>However, if the key string contains {@code secret}, the value will be masked.</p>
	 * 
	 * @since 1.0
	 */
	public static void logAllProperties() {
		log.info("======== Environment Variables ========");
		
		try {
			System.getenv().entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry::getKey))
				.map(InitializationUtil::toLogMessage)
				.forEach(log::info);
		} catch (Exception e) {
			log.info("unexpected", e);
		}
		
		log.info("======== System Properties ========");
		
		try {
			System.getProperties().entrySet().stream()
				.collect(Collectors.toMap(InitializationUtil::toStringKey, Map.Entry::getValue)).entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry::getKey))
				.map(InitializationUtil::toLogMessage)
				.forEach(log::info);
		} catch (Exception e) {
			log.info("unexpected", e);
		}
		
		log.info("===================================");
	}
	
	private static String toStringKey(Map.Entry<?, ?> e) {
		return Objects.toString(e.getKey());
	}
	
	private static String toLogMessage(Map.Entry<String, ?> e) {
		String key = e.getKey();
		String value = isMaskingRequired(key) ? MASK_STRING : Objects.toString(e.getValue());
		String clear = isClearEscapeSequenceRequired(value) ? "\033[m" : "";
		return String.format("%s = %s%s", key, value, clear);
	}
	
	private static boolean isClearEscapeSequenceRequired(String value) {
		return value.contains("\033");
	}
	
	private static boolean isMaskingRequired(String key) {
		key = key.toLowerCase();
		return key.contains("secret") || key.contains("password");
	}
	
	/**
	 * Validate if all system properties required by application is specified.
	 * 
	 * @param requiredSystemProperties list of required system property keys
	 * @throws IllegalStateException if exists insufficient properties
	 * @since 1.0
	 */
	public static void validateExistRequiredSystemProperties(Collection<String> requiredSystemProperties) {
		Properties properties = System.getProperties();
		String shortageProperties = requiredSystemProperties.stream().parallel()
			.filter(key -> properties.containsKey(key) == false)
			.collect(Collectors.joining(", "));
		if (shortageProperties.isEmpty() == false) {
			throw new IllegalStateException("required properties are not specified: " + shortageProperties);
		}
	}
}
