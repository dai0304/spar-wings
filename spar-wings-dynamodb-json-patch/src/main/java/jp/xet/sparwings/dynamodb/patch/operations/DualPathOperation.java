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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * Base class for JSON Patch operations taking two JSON Pointers as arguments
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public abstract class DualPathOperation extends JsonPatchOperation {
	
	@JsonSerialize(using = ToStringSerializer.class)
	protected JsonPointer from;
	
	
	/**
	 * Protected constructor
	 *
	 * @param op operation name
	 * @param from source path
	 * @param path destination path
	 */
	protected DualPathOperation(String op, JsonPointer from, JsonPointer path) {
		super(op, path);
		this.from = from;
	}
	
	@Override
	public void serialize(JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("op", op);
		jgen.writeStringField("path", path.toString());
		jgen.writeStringField("from", from.toString());
		jgen.writeEndObject();
	}
	
	@Override
	public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
			throws IOException, JsonProcessingException {
		serialize(jgen, provider);
	}
}
