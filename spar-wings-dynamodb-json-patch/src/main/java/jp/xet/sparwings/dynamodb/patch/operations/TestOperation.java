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

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.NULLComparable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.jsonpointer.JsonPointer;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.BOOL;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;


/**
 * JSON Patch {@code test} operation
 *
 * <p>The two arguments for this operation are the pointer containing the value
 * to test ({@code path}) and the value to test equality against ({@code
 * value}).</p>
 *
 * <p>It is an error if no value exists at the given path.</p>
 *
 * <p>Also note that equality as defined by JSON Patch is exactly the same as it
 * is defined by JSON Schema itself. As such, this operation reuses {@link
 * JsonNumEquals} for testing equality.</p>
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
public class TestOperation extends PathValueOperation {
	
	@JsonCreator
	public TestOperation(@JsonProperty("path") JsonPointer path, @JsonProperty("value") JsonNode value) {
		super("test", path, value);
	}

	public TestOperation(com.github.fge.jsonpatch.TestOperation o) {
		super(o);
	}
	
	@Override
	public void applyToBuilder(ExpressionSpecBuilder builder) {
		String attributePath = pathGenerator.apply(path);
		JsonNodeType type = value.getNodeType();
		switch (type) {
			case NUMBER:
				builder.withCondition(N(attributePath).eq(value.numberValue()));
				break;

			case STRING:
				builder.withCondition(S(attributePath).eq(value.textValue()));
				break;

			case BOOLEAN:
				builder.withCondition(BOOL(attributePath).eq(value.booleanValue()));
				break;

			case NULL:
				builder.withCondition(new NULLComparable(attributePath).eq(NULLComparable.generateNull()));
				break;

			case ARRAY:
				throw new UnsupportedOperationException("DynamoDB only supports conditions on scalars, not lists");

			case OBJECT:
				throw new UnsupportedOperationException("DynamoDB only supports conditions on scalars, not maps");

			default:
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException("Not implemented yet: " + type);
		}
	}
}
