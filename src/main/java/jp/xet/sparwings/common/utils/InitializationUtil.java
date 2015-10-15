/*
 * Copyright 2015 Miyamoto Daisuke.
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
	
	private static Logger logger = LoggerFactory.getLogger(InitializationUtil.class);
	
	
	/**
	 * Log all system properties.
	 * 
	 * <p>However, if the key string contains {@code secret}, the value will be masked.</p>
	 * 
	 * @since 1.0
	 */
	public static void logAllProperties() {
		logger.info("======== Environment Variables ========");
		
		try {
			System.getenv().entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getKey().toString()))
				.map(e -> {
					String key = e.getKey().toString();
					Object value = e.getKey().toString().toLowerCase().contains("secret") ? "********" : e.getValue();
					return String.format("%s = %s\033[m", key, value);
				})
				.forEach(logger::info);
		} catch (Exception e) {
			logger.info("unexpected", e);
		}
		
		logger.info("======== System Properties ========");
		
		try {
			System.getProperties().entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getKey().toString()))
				.map(e -> {
					String key = e.getKey().toString();
					Object value = e.getKey().toString().toLowerCase().contains("secret") ? "********" : e.getValue();
					return String.format("%s = %s", key, value);
				})
				.forEach(logger::info);
		} catch (Exception e) {
			logger.info("unexpected", e);
		}
		
		logger.info("===================================");
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
		String shortageProperties = requiredSystemProperties.stream()
			.parallel()
			.filter(key -> properties.containsKey(key) == false)
			.collect(Collectors.joining(", "));
		if (shortageProperties.isEmpty() == false) {
			throw new IllegalStateException("required properties are not specified: " + shortageProperties);
		}
	}
}
