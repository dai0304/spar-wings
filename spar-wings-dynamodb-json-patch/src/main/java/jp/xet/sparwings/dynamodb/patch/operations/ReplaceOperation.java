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

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.BOOL;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.L;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.M;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.NS;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.NULL;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.SS;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * JSON Patch {@code replace} operation
 *
 * <p>For this operation, {@code path} points to the value to replace, and
 * {@code value} is the replacement value.</p>
 *
 * <p>It is an error condition if {@code path} does not point to an actual JSON
 * value.</p>
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
public class ReplaceOperation extends PathValueOperation { // NOPMD - cc
	
	@JsonCreator
	public ReplaceOperation(@JsonProperty("path") JsonPointer path, @JsonProperty("value") JsonNode value) {
		super("replace", path, value);
	}
	
	@Override
	public void applyToBuilder(ExpressionSpecBuilder builder) { // NOPMD - cc
		String attributePath = pathGenerator.apply(getPath());
		JsonNode value = getValue();
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
	
	private Map<String, ?> toMap(JsonNode value) { // NOPMD - cc
		Map<String, Object> m = new LinkedHashMap<>();
		for (Iterator<Entry<String, JsonNode>> iterator = value.fields(); iterator.hasNext();) {
			Entry<String, JsonNode> e = iterator.next();
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
