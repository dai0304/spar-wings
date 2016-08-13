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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.*;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.M;

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

	/**
	 * creates a sparwings path value operation from a github path value operation
	 * @param pvo the path value operation to convert
	 */
	protected PathValueOperation(com.github.fge.jsonpatch.PathValueOperation pvo) {
		super(pvo);
		value = getProtected(com.github.fge.jsonpatch.PathValueOperation.class, "value", pvo);
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

	@Override
	public void applyToBuilder(ExpressionSpecBuilder builder) {
		String attributePath = pathGenerator.apply(path);
		JsonNodeType type = value.getNodeType();
		switch (type) {
			case NUMBER:
				builder.addUpdate(N(attributePath).set(value.numberValue()));
				break;

			case STRING:
				builder.addUpdate(S(attributePath).set(value.textValue()));
				break;

			case BOOLEAN:
				builder.addUpdate(BOOL(attributePath).set(value.booleanValue()));
				break;

			case NULL:
				builder.addUpdate(NULL(attributePath).set());
				break;

			case ARRAY:
				if (value.iterator().hasNext() == false) {
					builder.addUpdate(L(attributePath).set(Collections.emptyList()));
				} else {
					JsonNode repNode = value.iterator().next();
					if (repNode.isNumber()) {
						Set<Number> ns = StreamSupport.stream(value.spliterator(), false)
								.map(JsonNode::numberValue)
								.collect(Collectors.toSet());
						builder.addUpdate(NS(attributePath).set(ns));
					} else if (repNode.isTextual()) {
						Set<String> ss = StreamSupport.stream(value.spliterator(), false)
								.map(JsonNode::textValue)
								.collect(Collectors.toSet());
						builder.addUpdate(SS(attributePath).set(ss));
					} else {
						throw new UnsupportedOperationException("Not implemented yet: " + repNode.getNodeType());
					}
				}
				break;

			case OBJECT:
				Map<String, ?> m = toMap(value);
				builder.addUpdate(M(attributePath).set(m));
				break;

			default:
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException("Not implemented yet: " + type);
		}
	}

	private static Map<String, ?> toMap(JsonNode value) {
		Map<String, Object> m = new LinkedHashMap<>();
		for (Iterator<Map.Entry<String, JsonNode>> iterator = value.fields(); iterator.hasNext();) {
			Map.Entry<String, JsonNode> e = iterator.next();
			JsonNodeType nodeType = e.getValue().getNodeType();
			if (nodeType.equals(JsonNodeType.OBJECT)) {
				m.put(e.getKey(), toMap(e.getValue()));
			} else if (nodeType.equals(JsonNodeType.BOOLEAN)) {
				m.put(e.getKey(), e.getValue().booleanValue());
			} else if (nodeType.equals(JsonNodeType.NUMBER)) {
				m.put(e.getKey(), e.getValue().numberValue());
			} else if (nodeType.equals(JsonNodeType.STRING)) {
				m.put(e.getKey(), e.getValue().textValue());
			} else if (nodeType.equals(JsonNodeType.ARRAY)) {
				if (e.getValue().iterator().hasNext() == false) {
					m.put(e.getKey(), Collections.emptyList());
				} else {
					JsonNode repNode = e.getValue().iterator().next();
					if (repNode.isNumber()) {
						Set<Number> ns = StreamSupport.stream(e.getValue().spliterator(), false)
								.map(JsonNode::numberValue)
								.collect(Collectors.toSet());
						m.put(e.getKey(), ns);
					} else if (repNode.isTextual()) {
						Set<String> ss = StreamSupport.stream(e.getValue().spliterator(), false)
								.map(JsonNode::textValue)
								.collect(Collectors.toSet());
						m.put(e.getKey(), ss);
					} else {
						throw new UnsupportedOperationException("Not implemented yet: " + repNode.getNodeType());
					}
				}
			}
		}
		return m;
	}
}
