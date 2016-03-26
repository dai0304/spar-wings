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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.UpdateItemExpressionSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
public class JsonPatchToExpressionSpecBuilderReplaceIT {
	
	private static Logger logger = LoggerFactory.getLogger(JsonPatchToExpressionSpecBuilderReplaceIT.class);
	
	private static final String KEY_ATTRIBUTE_NAME = "key";
	
	private static final String VALUE = "keyValue";
	
	private static final PrimaryKey PK = new PrimaryKey(KEY_ATTRIBUTE_NAME, VALUE);
	
	JsonPatchToExpressionSpecBuilder sut = new JsonPatchToExpressionSpecBuilder();
	
	private Table table;
	
	
	@Before
	public void setUp() throws Exception {
		try {
			AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();
			amazonDynamoDB.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
			table = new Table(amazonDynamoDB, "json_patch_test");
			table.deleteItem(PK);
		} catch (AmazonServiceException e) {
			throw new AssumptionViolatedException(null, e);
		}
	}
	
	/**
	 * 存在しないitemに対するupdate。新規アイテム作成となる。
	 */
	@Test
	public void test_replace_singlePath_number() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a\", \"value\": 1 } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a", equalTo(1)));
	}
	
	/**
	 * 既存属性の既存オブジェクトへの値の追加。
	 */
	@Test
	public void test_replace_nestedPath_string() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", ImmutableMap.of("a", 1))
			.build()));
		
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a/b\", \"value\": \"foo\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a.a", equalTo(1)));
		assertThat(json, hasJsonPath("$.a.b", equalTo("foo")));
	}
	
	/**
	 * 既存属性の既存オブジェクトに対する値の更新。
	 */
	@Test
	public void test_replace_existingNestedPath_string() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", ImmutableMap.of("a", 2, "b", true))
			.build()));
		
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a/b\", \"value\": \"bar\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a.a", equalTo(2)));
		assertThat(json, hasJsonPath("$.a.b", equalTo("bar")));
	}
	
	/**
	 * 既存属性の既存スカラに対する属性値の追加要求。
	 */
	@Test(expected = AmazonServiceException.class)
	public void test_replace_property_toScalar_string() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", 1)
			.build()));
		
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a/b\", \"value\": \"bar\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
	}
	
	@Test
	public void test_replace_singlePath_numberSet() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a\", \"value\": [1,2] } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a[*]", hasItems(1, 2)));
	}
	
	@Test
	public void test_replace_singlePath_stringSet() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a\", \"value\": [\"foo\",\"bar\"] } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a[*]", hasItems("foo", "bar")));
	}
	
	@Test
	public void test_replace_replaceExisting_singlePath_stringSet() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", ImmutableSet.of("foo", "bar"))
			.build()));
		
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a\", \"value\": [\"baz\",\"qux\"] } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a[*]", hasSize(2)));
		assertThat(json, hasJsonPath("$.a[*]", hasItems("baz", "qux")));
	}
	
	@Test
	public void test_replace_singlePath_object() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"replace\", \"path\": \"/a\", \"value\": {\"b\": \"c\", \"d\": 1} } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder builder = sut.apply(jsonPatch);
		UpdateItemExpressionSpec spec = builder.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, spec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a.b", equalTo("c")));
		assertThat(json, hasJsonPath("$.a.d", equalTo(1)));
	}
	
}
