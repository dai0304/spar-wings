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

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.NULL;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.UpdateItemExpressionSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import org.junit.Test;

/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
public class JsonPatchToExpressionSpecBuilderTest {
	
	JsonPatchToExpressionSpecBuilder sut = new JsonPatchToExpressionSpecBuilder();
	
	
	@Test
	public void testEmpty() throws Exception {
		// setup
		String patchExpression = "[]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		// verify
		assertThat(actual, is(notNullValue()));
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		assertThat(actualSpec.getConditionExpression(), is(nullValue()));
		assertThat(actualSpec.getUpdateExpression(), is(""));
		assertThat(actualSpec.getNameMap(), is(nullValue()));
		assertThat(actualSpec.getValueMap(), is(nullValue()));
	}
	
	@Test
	public void test_add_singlePath_number() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"add\", \"path\": \"/a\", \"value\": 1 } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		UpdateItemExpressionSpec expectedSpec = new ExpressionSpecBuilder()
			.addUpdate(N("a").set(1))
			.buildForUpdate();
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		// verify
		assertThat(actual, is(notNullValue()));
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		assertThat(actualSpec.getConditionExpression(), is(nullValue()));
		assertThat(actualSpec.getUpdateExpression(), is(expectedSpec.getUpdateExpression()));
		assertThat(actualSpec.getNameMap(), is(expectedSpec.getNameMap()));
		assertThat(actualSpec.getValueMap(), is(expectedSpec.getValueMap()));
	}
	
	@Test
	public void test_add_nestedPath_string() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"add\", \"path\": \"/a/b\", \"value\": \"foo\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		UpdateItemExpressionSpec expectedSpec = new ExpressionSpecBuilder()
			.addUpdate(S("a.b").set("foo"))
			.buildForUpdate();
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		// verify
		assertThat(actual, is(notNullValue()));
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		assertThat(actualSpec.getConditionExpression(), is(nullValue()));
		assertThat(actualSpec.getUpdateExpression(), is(expectedSpec.getUpdateExpression()));
		assertThat(actualSpec.getNameMap(), is(expectedSpec.getNameMap()));
		assertThat(actualSpec.getValueMap(), is(expectedSpec.getValueMap()));
	}
	
	@Test
	public void test_remove_singlePath() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"remove\", \"path\": \"/a\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		UpdateItemExpressionSpec expectedSpec = new ExpressionSpecBuilder()
			.addUpdate(NULL("a").remove())
			.buildForUpdate();
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		// verify
		assertThat(actual, is(notNullValue()));
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		assertThat(actualSpec.getConditionExpression(), is(nullValue()));
		assertThat(actualSpec.getUpdateExpression(), is(expectedSpec.getUpdateExpression()));
		assertThat(actualSpec.getNameMap(), is(expectedSpec.getNameMap()));
		assertThat(actualSpec.getValueMap(), is(expectedSpec.getValueMap()));
	}
	
}
