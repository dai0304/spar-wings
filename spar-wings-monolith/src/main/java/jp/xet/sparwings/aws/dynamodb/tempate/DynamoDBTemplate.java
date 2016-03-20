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
package jp.xet.sparwings.aws.dynamodb.tempate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * <b>This is the central class in the DynamoDB Template package.</b>
 * It simplifies the use of DynamoDB and helps to avoid common errors.
 * It executes core DynamoDB workflow, leaving application code to provide query
 * and extract results. This class executes DynamoDB queries or updates.
 *
 * @since 0.3
 * @author daisuke
 */
public class DynamoDBTemplate {
	
	private static Logger logger = LoggerFactory.getLogger(DynamoDBTemplate.class);
	
	
	/**
	 * Assert that an object is not {@code null}.
	 * 
	 * <pre class="code">notNull(clazz, "The class must not be null");</pre>
	 * 
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 * @since 0.3
	 */
	private static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	private static String renderKey(Map<String, ?> key) {
		return key.toString();
	}
	
	
	private final AmazonDynamoDB client;
	
	
	/**
	 * Create the instance.
	 * 
	 * @param client {@link AmazonDynamoDB} client
	 * @since 0.3
	 */
	public DynamoDBTemplate(AmazonDynamoDB client) {
		notNull(client, "AmazonDynamoDBClient must not be null");
		this.client = client;
	}
	
	/**
	 * Get single item by the {@code key}.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param key item key
	 * @param consistentRead A value that if set to <code>true</code>, then the operation uses strongly consistent reads;
	 *     otherwise, eventually consistent reads are used.
	 * @param projectionExpression A string that identifies one or more attributes to retrieve from the table. These
	 *     attributes can include scalars, sets, or elements of a JSON document. The attributes in the expression must
	 *     be separated by commas.  If this is empty, then all attributes will be returned. If any of the requested
	 *     attributes are not found, they will not appear in the result.
	 * @param extractor to convert attribute map to the entity
	 * @param <T> the type of extract
	 * @return item extracted by extractor
	 * @throws EmptyResultDataAccessException if the result was not found
	 * @see AmazonDynamoDB#getItem(GetItemRequest)
	 * @since 0.3
	 */
	public <T>T get(String tableName, Map<String, AttributeValue> key, Boolean consistentRead,
			Optional<String> projectionExpression, ObjectExtractor<T> extractor, String... columnsToInclude)
			throws EmptyResultDataAccessException {
		notNull(tableName, "tableName must not be null");
		notNull(extractor, "extractor must not be null");
		logger.debug("Executing query on {} for {}", tableName, renderKey(key));
		
		GetItemRequest request = new GetItemRequest(tableName, key, consistentRead);
		projectionExpression.ifPresent(request::setProjectionExpression);
		if (columnsToInclude != null && columnsToInclude.length > 0) {
			request.setAttributesToGet(Arrays.asList(columnsToInclude));
		}
		
		GetItemResult result = client.getItem(request);
		
		Map<String, AttributeValue> item = result.getItem();
		if (item == null) {
			throw new EmptyResultDataAccessException("No results found in " + tableName + " for " + renderKey(key));
		}
		return extractor.extract(item);
	}
	
	/**
	 * Get multiple items by the {@code keysAndAttributes}.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param keysAndAttributes keys for items
	 * @param extractor to convert attribute map to the entity
	 * @return items extracted by extractor
	 * @param <T> the type of extract
	 * @throws EmptyResultDataAccessException if the result was not found
	 * @see AmazonDynamoDB#batchGetItem(com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest)
	 * @since 0.3
	 */
	public <T>List<T> batchGet(String tableName, KeysAndAttributes keysAndAttributes, ObjectExtractor<T> extractor)
			throws EmptyResultDataAccessException {
		notNull(tableName, "tableName must not be null");
		notNull(keysAndAttributes, "keysAndAttributes must not be null");
		notNull(extractor, "extractor must not be null");
		logger.debug("Executing batch get on {} for {}", tableName, keysAndAttributes.toString());
		
		List<T> results = new ArrayList<>(keysAndAttributes.getKeys().size());
		
		Map<String, KeysAndAttributes> unprocessedKeys = Collections.singletonMap(tableName, keysAndAttributes);
		while (unprocessedKeys.size() > 0) {
			BatchGetItemResult result = client.batchGetItem(unprocessedKeys);
			List<T> items = result.getResponses().get(tableName).stream()
				.map(extractor::extract)
				.collect(Collectors.toList());
			results.addAll(items);
			unprocessedKeys = result.getUnprocessedKeys();
		}
		
		if (results.size() == 0) {
			throw new EmptyResultDataAccessException("No results found in " + tableName
					+ "for " + keysAndAttributes.toString());
		}
		return results;
	}
	
	/**
	 * Query only one item by {@code keyConditions}.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param indexName The name of a secondary index to scan. This index can be any local secondary index or global
	 *     secondary index.
	 * @param keyConditions The selection criteria for the query. For a query on a table, you can have conditions
	 *     only on the table primary key attributes. You must provide the hash key attribute name and value as an
	 *     {@code EQ} condition. You can optionally provide a second condition, referring to the range key attribute.
	 * @param projectionExpression A string that identifies one or more attributes to retrieve from the table. These
	 *     attributes can include scalars, sets, or elements of a JSON document. The attributes in the expression must
	 *     be separated by commas.  If this is empty, then all attributes will be returned. If any of the requested
	 *     attributes are not found, they will not appear in the result.
	 * @param extractor to convert attribute map to the entity
	 * @param <T> the type of extract
	 * @return item extracted by extractor
	 * @throws EmptyResultDataAccessException if the result was not found
	 * @throws TooManyResultDataAccessException if multiple items found as query result
	 * @since 0.3
	 */
	public <T>T queryUnique(String tableName, Optional<String> indexName, Map<String, Condition> keyConditions,
			Optional<String> projectionExpression, ObjectExtractor<T> extractor, String... columnsToInclude)
			throws EmptyResultDataAccessException, TooManyResultDataAccessException {
		List<T> items = query(tableName, indexName, keyConditions, projectionExpression, extractor, columnsToInclude);
		
		if (items.size() == 0) {
			throw new EmptyResultDataAccessException("No results found in " + tableName
					+ "for " + renderKey(keyConditions));
		} else if (items.size() > 1) {
			throw new TooManyResultDataAccessException("Expecting 1 result for " + renderKey(keyConditions)
					+ " but found " + items.size());
		}
		return items.iterator().next();
	}
	
	/**
	 * Query one or more items by {@code keyConditions}.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param indexName The name of a secondary index to scan. This index can be any local secondary index or global
	 *     secondary index.
	 * @param keyConditions The selection criteria for the query. For a query on a table, you can have conditions
	 *     only on the table primary key attributes. You must provide the hash key attribute name and value as an
	 *     {@code EQ} condition. You can optionally provide a second condition, referring to the range key attribute.
	 * @param projectionExpression A string that identifies one or more attributes to retrieve from the table. These
	 *     attributes can include scalars, sets, or elements of a JSON document. The attributes in the expression must
	 *     be separated by commas.  If this is empty, then all attributes will be returned. If any of the requested
	 *     attributes are not found, they will not appear in the result.
	 * @param extractor to convert attribute map to the entity
	 * @param columnsToInclude
	 * @param <T> the type of extract
	 * @return items extracted by extractor
	 * @throws EmptyResultDataAccessException if the result was not found
	 * @see AmazonDynamoDB#query(QueryRequest)
	 * @since 0.3
	 */
	public <T>List<T> query(String tableName, Optional<String> indexName, Map<String, Condition> keyConditions,
			Optional<String> projectionExpression, ObjectExtractor<T> extractor, String... columnsToInclude)
			throws EmptyResultDataAccessException {
		notNull(tableName, "tableName must not be null");
		notNull(extractor, "extractor must not be null");
		logger.debug("Executing query on {} for {}", tableName, renderKey(keyConditions));
		
		QueryRequest request = new QueryRequest(tableName)
			.withConsistentRead(false) // because query is used on GSIs where consistent reads are not supported.
			.withKeyConditions(keyConditions);
		projectionExpression.ifPresent(request::setProjectionExpression);
		indexName.ifPresent(request::setIndexName);
		if (columnsToInclude != null && columnsToInclude.length > 0) {
			request.setAttributesToGet(Arrays.asList(columnsToInclude));
		}
		
		QueryResult result = client.query(request);
		
		return result.getItems().stream()
			.map(extractor::extract)
			.collect(Collectors.toList());
	}
	
	/**
	 * Scan table.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param indexName The name of a secondary index to scan. This index can be any local secondary index or global
	 *     secondary index.
	 * @param limit The maximum number of items to evaluate (not necessarily the number of matching items).
	 *     If DynamoDB processes the number of items up to the limit while processing the results, it stops
	 *     the operation and returns the matching values up to that point
	 * @param projectionExpression A string that identifies one or more attributes to retrieve from the table. These
	 *     attributes can include scalars, sets, or elements of a JSON document. The attributes in the expression must
	 *     be separated by commas.  If this is empty, then all attributes will be returned. If any of the requested
	 *     attributes are not found, they will not appear in the result.
	 * @param extractor to convert attribute map to the entity
	 * @param <T> the type of extract
	 * @return items extracted by extractor
	 * @throws EmptyResultDataAccessException if the result was not found
	 * @see AmazonDynamoDB#scan(ScanRequest)
	 * @since 0.3
	 */
	public <T>List<T> scan(String tableName, Optional<String> indexName, Optional<Integer> limit,
			Optional<String> projectionExpression, ObjectExtractor<T> extractor) throws EmptyResultDataAccessException {
		notNull(tableName, "tableName must not be null");
		notNull(extractor, "extractor must not be null");
		logger.debug("Executing scan on {} for {}", tableName, limit);
		
		ScanRequest request = new ScanRequest(tableName);
		indexName.ifPresent(request::setIndexName);
		limit.ifPresent(request::setLimit);
		projectionExpression.ifPresent(request::setProjectionExpression);
		
		ScanResult result = client.scan(request);
		
		return result.getItems().stream()
			.map(extractor::extract)
			.collect(Collectors.toList());
	}
	
	/**
	 * Edits an existing item's attributes, or adds a new item to the table
	 * if it does not already exist. You can put, delete, or add attribute values.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param key The primary key of the item to be updated.
	 * @param updateExpression An expression that defines one or more attributes to be updated, the
	 *         action to be performed on them, and new value(s) for them.
	 * @param expressionAttributeValues One or more values that can be substituted in an expression.
	 * @see AmazonDynamoDB#updateItem(com.amazonaws.services.dynamodbv2.model.UpdateItemRequest)
	 * @since 0.3
	 */
	public void update(String tableName, Map<String, AttributeValue> key, String updateExpression,
			Map<String, AttributeValue> expressionAttributeValues) {
		notNull(tableName, "tableName must not be null");
		logger.debug("Update item on {} for {} as {}", tableName, renderKey(key), updateExpression);
		
		UpdateItemRequest request = new UpdateItemRequest()
			.withTableName(tableName)
			.withKey(key)
			.withUpdateExpression(updateExpression)
			.withExpressionAttributeValues(expressionAttributeValues);
		client.updateItem(request);
	}
	
	@Deprecated
	@SuppressWarnings("javadoc")
	public void update(String tableName, Map<String, AttributeValue> key, Map<String, AttributeValueUpdate> updates) {
		Assert.notNull(tableName, "tableName must not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Update item on {} for {} as {}", tableName, renderKey(key), updates);
		}
		client.updateItem(tableName, key, updates);
	}
	
	/**
	 * Deletes a single item in a table by primary key.
	 * 
	 * @param tableName the name of DynamoDB table
	 * @param key The primary key of the item to be deleted.
	 * @see AmazonDynamoDB#deleteItem(com.amazonaws.services.dynamodbv2.model.DeleteItemRequest)
	 * @since 0.3
	 */
	public void delete(String tableName, Map<String, AttributeValue> key) {
		notNull(tableName, "tableName must not be null");
		logger.debug("Delete item on {} for {}", tableName, renderKey(key));
		
		client.deleteItem(tableName, key);
	}
}
