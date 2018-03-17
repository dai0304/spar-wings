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
package jp.xet.sparwings.dynamodb.patch;

import java.io.IOException;
import java.util.List;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.JacksonUtils;
import com.google.common.collect.ImmutableList;

import jp.xet.sparwings.dynamodb.patch.operations.JsonPatchOperation;

/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
public class JsonPatch implements JsonSerializable {
	
	/**
	 * List of operations
	 */
	@Getter
	private List<JsonPatchOperation> operations;
	
	
	/**
	 * Constructor
	 *
	 * <p>Normally, you should never have to use it.</p>
	 *
	 * @param operations the list of operations for this patch
	 * @see JsonPatchOperation
	 */
	@JsonCreator
	public JsonPatch(List<JsonPatchOperation> operations) {
		this.operations = ImmutableList.copyOf(operations);
	}
	
	/**
	 * Static factory method to build a JSON Patch out of a JSON representation
	 *
	 * @param node the JSON representation of the generated JSON Patch
	 * @return a JSON Patch
	 * @throws IOException input is not a valid JSON patch
	 * @throws NullPointerException input is null
	 */
	public static JsonPatch fromJson(JsonNode node) throws IOException {
		return JacksonUtils.getReader().forType(JsonPatch.class)
			.readValue(node);
	}
	
	@Override
	public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartArray();
		for (JsonPatchOperation op : operations) {
			op.serialize(jgen, provider);
		}
		jgen.writeEndArray();
	}
	
	@Override
	public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
			throws IOException {
		serialize(jgen, provider);
	}
}
