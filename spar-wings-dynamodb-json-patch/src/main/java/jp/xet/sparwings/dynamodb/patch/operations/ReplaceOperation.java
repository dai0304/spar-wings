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
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
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
 * @author Alexander Patrikalakis
 */
public class ReplaceOperation extends PathValueOperation {
	
	@JsonCreator
	public ReplaceOperation(@JsonProperty("path") JsonPointer path, @JsonProperty("value") JsonNode value) {
		super("replace", path, value);
	}

	public ReplaceOperation(com.github.fge.jsonpatch.ReplaceOperation o) {
		super(o);
	}
}
