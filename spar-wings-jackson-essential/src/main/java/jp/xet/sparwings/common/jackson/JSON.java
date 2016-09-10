/*
 * Copyright 2015-2016 Classmethod, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All source code, documentation and other information
 * contained herein is, and remains the property of Classmethod, Inc.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Classmethod, Inc.
 */
package jp.xet.sparwings.common.jackson;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON Utility.
 * 
 * @author arikawa.eichi
 * @since #version#
 */
@Slf4j
@UtilityClass
public class JSON {
	
	@NonNull
	@Setter
	@Getter
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	
	/**
	 * The {@code JSON.stringify()} method converts a model value to a JSON string.
	 * 
	 * @param value The value to convert to a JSON string.
	 * @return JSON String
	 * @throws JsonProcessingException if processing failed.
	 * @since #version#
	 */
	public static String stringify(Object value) {
		try {
			String result = objectMapper.writeValueAsString(value);
			return result;
		} catch (Exception e) {
			log.error("JSON fails stringify value: {}", value, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The {@code JSON.parse()} method parses a JSON string as model value.
	 * 
	 * @param json The string to parse as JSON.
	 * @param clazz The clazz to mapping.
	 * @return Returns the clazz instance corresponding to the given JSON text.
	 * @throws JsonProcessingException if processing failed.
	 * @since #version#
	 */
	public static <T> T parse(String json, Class<T> clazz) {
		try {
			T result = objectMapper.readValue(json, clazz);
			return result;
		} catch (Exception e) {
			log.error("JSON fails parse string to {}: {}", clazz, json, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The {@code JSON.parse()} method parses a JSON string as {@link JsonNode}.
	 * 
	 * @param json The string to parse as JSON.
	 * @return JsonNode
	 * @throws JsonProcessingException if processing failed.
	 * @since #version#
	 */
	public static JsonNode parse(String json) {
		try {
			JsonNode result = objectMapper.readTree(json);
			return result;
		} catch (Exception e) {
			log.error("JSON fails parse string to JsonNode: {}", json, e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The {@code JSON.parse()} method parses a JSON string as {@link Map}.
	 * 
	 * @param json The string to parse as JSON.
	 * @return Map
	 * @throws JsonProcessingException if processing failed.
	 * @since #version#
	 */
	public static Map<String, Object> parseAsMap(String json) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = parse(json, Map.class);
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JsonProcessingException(e);
		}
	}
	
	
	/**
	 * Exception thrown by {@link JSON} if processing failed.
	 * 
	 * @since #version#
	 * @version $Id$
	 * @author daisuke
	 */
	@SuppressWarnings("serial")
	public static class JsonProcessingException extends RuntimeException {
		
		JsonProcessingException(Throwable cause) {
			super(cause);
		}
	}
}
