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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import jp.xet.sparwings.dynamodb.patch.operations.AddOperation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.jsonpointer.JsonPointer;

import org.junit.Test;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class JsonPatchTest {
	
	@Test
	public void test_single_add() throws Exception {
		// setup
		String patchExpression = "[ { \"op\": \"add\", \"path\": \"/a\", \"value\": 1 } ]";
		JsonNode jsonNode = JsonLoader.fromString(patchExpression);
		// exercise
		JsonPatch actual = JsonPatch.fromJson(jsonNode);
		// verify
		assertThat(actual.getOperations().size(), is(1));
		AddOperation operation = (AddOperation) actual.getOperations().get(0);
		assertThat(operation.getOp(), is("add"));
		assertThat(operation.getPath(), is(new JsonPointer("/a")));
		assertThat(operation.getValue(), is(JsonLoader.fromString("1")));
	}
}
