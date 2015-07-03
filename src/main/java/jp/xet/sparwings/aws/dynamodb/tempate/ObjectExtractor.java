/*
 * Copyright 2015 Miyamoto Daisuke, Inc.
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

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * Implementations of this interface are responsible for converting DynamoDB results returned by
 * a get/query operation to their corresponding Object [T] representation.
 * 
 * @param <T> type of extract
 * @since #version#
 * @author daisuke
 */
public interface ObjectExtractor<T> {
	
	/**
	 * TODO for daisuke
	 * 
	 * @param values {@link AttributeValue} map
	 * @return extract
	 * @since #version#
	 */
	T extract(Map<String, AttributeValue> values);
	
}
