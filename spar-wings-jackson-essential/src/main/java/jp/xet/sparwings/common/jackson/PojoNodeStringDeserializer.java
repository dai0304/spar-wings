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
package jp.xet.sparwings.common.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.util.RawValue;

class PojoNodeStringDeserializer extends JsonDeserializer<String> {
	
	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		TreeNode tree = jp.readValueAsTree();
		if (tree instanceof POJONode) {
			POJONode pojoNode = (POJONode) tree;
			Object pojo = pojoNode.getPojo();
			if (pojo instanceof RawValue) {
				RawValue rawValue = (RawValue) pojo;
				Object value = rawValue.rawValue();
				return value.toString();
			}
		}
		return tree.toString();
	}
}
