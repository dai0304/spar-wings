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

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.NULL;

import java.beans.Expression;
import java.io.IOException;

import lombok.ToString;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * JSON Path {@code remove} operation.
 *
 * <p>This operation only takes one pointer ({@code path}) as an argument. It
 * is an error condition if no JSON value exists at that pointer.</p>
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
@ToString
public class RemoveOperation extends JsonPatchOperation {
	
	@JsonCreator
	public RemoveOperation(@JsonProperty("path") JsonPointer path) {
		super("remove", path);
	}
	
	@Override
	public void serialize(JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("op", "remove");
		jgen.writeStringField("path", path.toString());
		jgen.writeEndObject();
	}

	public RemoveOperation(com.github.fge.jsonpatch.RemoveOperation o) {
		super(o);
	}
	
	@Override
	public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
			throws IOException, JsonProcessingException {
		serialize(jgen, provider);
	}
	
	@Override
	public void applyToBuilder(ExpressionSpecBuilder builder) {
		String attributePath = pathGenerator.apply(getPath());
		builder.addUpdate(ExpressionSpecBuilder.remove(attributePath));
	}
}
