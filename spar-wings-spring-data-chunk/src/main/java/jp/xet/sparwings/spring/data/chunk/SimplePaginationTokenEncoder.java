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
package jp.xet.sparwings.spring.data.chunk;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple JSON and Base64 URL implementation for {@link PaginationTokenEncoder}.
 * 
 * @since 0.24
 * @version $Id$
 * @author daisuke
 */
@Slf4j
public class SimplePaginationTokenEncoder implements PaginationTokenEncoder {
	
	private static final ObjectMapper OM = new ObjectMapper();
	
	private static final String FIRST_KEY = "first_key";
	
	private static final String LAST_KEY = "last_key";
	
	
	@Override
	public Optional<String> extractFirstKey(String paginationToken) {
		if (paginationToken == null) {
			return Optional.empty();
		}
		byte[] json = Base64.getUrlDecoder().decode(paginationToken);
		try {
			JsonNode tree = OM.readTree(json);
			return Optional.ofNullable(tree.path(FIRST_KEY).asText(null));
		} catch (IOException e) {
			log.warn("Invalid pagination token: {}", paginationToken);
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<String> extractLastKey(String paginationToken) {
		if (paginationToken == null) {
			return Optional.empty();
		}
		byte[] json = Base64.getUrlDecoder().decode(paginationToken);
		try {
			JsonNode tree = OM.readTree(json);
			return Optional.ofNullable(tree.path(LAST_KEY).asText(null));
		} catch (IOException e) {
			log.warn("Invalid pagination token: {}", paginationToken);
		}
		return Optional.empty();
	}
	
	@Override
	public String encode(Object firstKey, Object lastKey) {
		Map<String, Object> map = new HashMap<>(2);
		map.put(FIRST_KEY, firstKey);
		map.put(LAST_KEY, lastKey);
		try {
			String json = OM.writeValueAsString(map);
			return Base64.getUrlEncoder().encodeToString(json.getBytes());
		} catch (JsonProcessingException e) {
			throw new InvalidKeyExpressionException(e);
		}
	}
}
