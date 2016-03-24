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
package jp.xet.sparwings.dynamodb.patch.operations;

import java.io.IOException;

import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * Base class for patch operations taking a value in addition to a path
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
@ToString
public abstract class PathValueOperation extends JsonPatchOperation {
	
	@Getter
	@JsonSerialize
	final JsonNode value;
	
	
	/**
	 * Protected constructor
	 *
	 * @param op operation name
	 * @param path affected path
	 * @param value JSON value
	 */
	protected PathValueOperation(String op, JsonPointer path, JsonNode value) {
		super(op, path);
		this.value = value.deepCopy();
	}
	
	@Override
	public final void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("op", op);
		jgen.writeStringField("path", path.toString());
		jgen.writeFieldName("value");
		jgen.writeTree(value);
		jgen.writeEndObject();
	}
	
	@Override
	public final void serializeWithType(JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer)
			throws IOException, JsonProcessingException {
		serialize(jgen, provider);
	}
}
