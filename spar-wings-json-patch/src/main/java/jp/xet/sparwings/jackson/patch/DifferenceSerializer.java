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
package jp.xet.sparwings.jackson.patch;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.fge.jsonpatch.diff.JsonDiff;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DifferenceSerializer extends JsonSerializer<Difference> {
	
	private final ObjectMapper mapper;
	
	
	@Override
	public void serialize(Difference value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		provider.defaultSerializeValue(JsonDiff.asJson(
				mapper.valueToTree(value.original),
				mapper.valueToTree(value.updated)), jgen);
	}
}
