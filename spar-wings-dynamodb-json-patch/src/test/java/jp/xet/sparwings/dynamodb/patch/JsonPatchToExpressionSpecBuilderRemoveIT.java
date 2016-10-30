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
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import com.amazonaws.AmazonClientException;
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

/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
public class JsonPatchToExpressionSpecBuilderRemoveIT {
	
	private static Logger logger = LoggerFactory.getLogger(JsonPatchToExpressionSpecBuilderRemoveIT.class);
	
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
		} catch (AmazonClientException e) {
			throw new AssumptionViolatedException(null, e);
		}
	}
	
	@Test
	public void test_remove_singlePath() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", ImmutableMap.of("a", 2, "b", true))
			.build()));
		
		// setup
		String patchExpression = "[ { \"op\": \"remove\", \"path\": \"/a\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, actualSpec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasNoJsonPath("$.a"));
	}
	
	@Test
	public void test_remove_nestedPath() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", ImmutableMap.of("a", 2, "b", true))
			.build()));
		
		// setup
		String patchExpression = "[ { \"op\": \"remove\", \"path\": \"/a/a\" } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, actualSpec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a.b", equalTo(true)));
		assertThat(json, hasNoJsonPath("$.a.a"));
	}
	
	@Test
	public void test_remove_absentPath() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", "b")
			.build()));
		
		// setup
		String patchExpression = "[ { \"op\": \"remove\", \"path\": \"/c\" } ]"; // $.c does not exist in target
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, actualSpec);
		// verify
		Item item = table.getItem(PK);
		String json = item.toJSON();
		logger.info("{}", json);
		assertThat(json, hasJsonPath("$.key", equalTo("keyValue")));
		assertThat(json, hasJsonPath("$.a", equalTo("b")));
	}
	
	@Test(expected = AmazonServiceException.class)
	public void test_remove_absentObjectPath() throws Exception {
		// setup
		table.putItem(Item.fromMap(ImmutableMap.<String, Object> builder()
			.put(KEY_ATTRIBUTE_NAME, VALUE)
			.put("a", "b")
			.build()));
		
		// setup
		String patchExpression = "[ { \"op\": \"remove\", \"path\": \"/c/d\" } ]"; // $.c does not exist in target
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);
		// exercise
		ExpressionSpecBuilder actual = sut.apply(jsonPatch);
		UpdateItemExpressionSpec actualSpec = actual.buildForUpdate();
		table.updateItem(KEY_ATTRIBUTE_NAME, VALUE, actualSpec);
	}
}
