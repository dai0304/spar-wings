/*
 * Copyright 2015 Miyamoto Daisuke.
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
package jp.xet.sparwings.aws.dynamodb;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;

/**
 * @author daisuke
 */
public class DynamoDBUtils {
	
	public static void nullSafeUpdateS(Map<String, AttributeValueUpdate> updates, String column, String value) {
		if (value == null || value.length() == 0) {
			updates.put(column, new AttributeValueUpdate().withAction(AttributeAction.DELETE));
		} else {
			updates.put(column, new AttributeValueUpdate(new AttributeValue(value), AttributeAction.PUT));
		}
	}
	
	public static String nullSafeGetS(AttributeValue value) {
		return value == null ? null : value.getS();
	}
}
