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

import java.util.function.Function;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class JsonPatchToExpressionSpecBuilder implements Function<JsonPatch, ExpressionSpecBuilder> {
	
	@Override
	public ExpressionSpecBuilder apply(JsonPatch t) {
		ExpressionSpecBuilder builder = new ExpressionSpecBuilder();
		t.getOperations().forEach(operation -> operation.applyToBuilder(builder));
		return builder;
	}
}
