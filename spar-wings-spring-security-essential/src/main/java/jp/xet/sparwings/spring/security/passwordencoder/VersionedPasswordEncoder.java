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
package jp.xet.sparwings.spring.security.passwordencoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.Assert;

/**
 * TODO for daisuke
 * 
 * @author daisuke
 * @since 0.12
 */
public class VersionedPasswordEncoder implements PasswordEncoder {
	
	private static Logger logger = LoggerFactory.getLogger(VersionedPasswordEncoder.class);
	
	private static final Pattern VERSION_KEY_PATTERN = Pattern.compile("\\A[0-9A-Za-z]+");
	
	private static final Pattern VERSION_ENCODED_PATTERN = Pattern.compile("\\A([0-9A-Za-z]+)_(.*)");
	
	private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
	
	private static final PasswordEncoder BCRYPT = new BCryptPasswordEncoder();
	
	private static final Pattern STANDARD_PATTERN = Pattern.compile("\\A[0-9A-Fa-f]+");
	
	private static final PasswordEncoder STANDARD = new StandardPasswordEncoder();
	
	private static final Map<String, PasswordEncoder> DEFAULT_ENCODERS;
	static {
		Map<String, PasswordEncoder> map = new HashMap<>();
		map.put("NoOp", NoOpPasswordEncoder.getInstance());
		map.put("Std", STANDARD);
		map.put("BC", BCRYPT);
		DEFAULT_ENCODERS = Collections.unmodifiableMap(map);
	}
	
	private final Map<String, PasswordEncoder> encoders;
	
	private final String primaryEncoder;
	
	private final String unknownEncoder;
	
	@Getter
	@Setter
	private boolean enableAutoDetection = true;
	
	
	/**
	 * Create instance with default encoder map.
	 * 
	 * @since 0.12
	 */
	public VersionedPasswordEncoder() {
		this("NoOp", "BC", DEFAULT_ENCODERS);
	}
	
	/**
	 * Create instance with custom encoder map.
	 * 
	 * @param unknownEncoder The map key for unknwon encoder
	 * @param primaryEncoder The map key for primary encoder
	 * @param encoders The map of {@link PasswordEncoder}
	 * @since 0.9
	 */
	public VersionedPasswordEncoder(String unknownEncoder, String primaryEncoder, Map<String, PasswordEncoder> encoders) {
		Assert.notNull(encoders);
		Assert.isTrue(encoders.containsKey(unknownEncoder), "The encoders must to contain unknown-encoder");
		Assert.isTrue(encoders.containsKey(primaryEncoder), "The encoders must to contain primary-encoder");
		if (encoders.keySet().stream().allMatch(key -> VERSION_KEY_PATTERN.matcher(key).matches()) == false) {
			throw new IllegalArgumentException("Some keys unmatches the key pattern.");
		}
		
		this.primaryEncoder = primaryEncoder;
		this.unknownEncoder = unknownEncoder;
		this.encoders = Collections.unmodifiableMap(encoders);
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		String encoded = encoders.get(primaryEncoder).encode(rawPassword);
		return String.format("%s_%s", primaryEncoder, encoded);
	}
	
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		try {
			Matcher matcher = VERSION_ENCODED_PATTERN.matcher(encodedPassword);
			if (matcher.matches()) {
				String key = matcher.group(1);
				String body = matcher.group(2);
				
				PasswordEncoder encoder;
				if (encoders.containsKey(key)) {
					encoder = encoders.get(key);
				} else {
					logger.warn("Unknown encoding type: {} ... using unknown encoder {}", key, unknownEncoder);
					encoder = encoders.get(unknownEncoder);
				}
				
				return encoder.matches(rawPassword, body);
			} else if (enableAutoDetection) {
				if (BCRYPT_PATTERN.matcher(encodedPassword).matches()) { // auto detect BCrypt
					logger.info("It seems to be BCrypt encoded password...");
					if (BCRYPT.matches(rawPassword, encodedPassword)) {
						return true;
					}
				}
				if (STANDARD_PATTERN.matcher(encodedPassword).matches()) { // auto detect Standard
					logger.info("It seems to be Standard encoded password...");
					if (STANDARD.matches(rawPassword, encodedPassword)) {
						return true;
					}
				}
			}
			return encoders.get(unknownEncoder).matches(rawPassword, encodedPassword);
		} catch (Exception e) {
			logger.warn("Unexpected", e);
		}
		return false;
	}
}
