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

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Test for {@link JsonRawValueExtended} annotation.
 * 
 * @since 0.27
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
public class JsonRawValueExtendedTest {
	
	private ObjectMapper mapper;
	
	
	@Before
	public void setup() throws Exception {
		mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
	}
	
	@Test
	public void testValueToJson() throws Exception {
		// setup
		Model model = new Model();
		model.setFoo("12341234");
		model.setBar(1234);
		model.setMetadata("{\"foo\":\"bar\"}");
		model.setHogeMetadata("{\"baz\":\"qux\"}");
		// exercise
		String actual = mapper.writeValueAsString(model);
		// verify
		with(actual)
			.assertThat("$.foo", is("12341234"))
			.assertThat("$.bar", is(1234))
			.assertThat("$.metadata.foo", is("bar"))
			.assertThat("$.hoge_metadata.baz", is("qux"));
	}
	
	@Test
	public void testValueToTreeToValue() throws Exception {
		// setup
		Model model = new Model();
		model.setFoo("12341234");
		model.setBar(1234);
		model.setMetadata("{\"foo\":\"bar\"}");
		model.setHogeMetadata("{\"baz\":\"qux\"}");
		
		// exercise 1: Value to Tree
		JsonNode actualNode = mapper.valueToTree(model);
		// verify 1
		assertThat(actualNode.path("foo").isTextual(), is(true));
		assertThat(actualNode.path("foo").textValue(), is("12341234"));
		assertThat(actualNode.path("bar").isNumber(), is(true));
		assertThat(actualNode.path("bar").numberValue(), is(1234));
		
		assertThat(actualNode.path("metadata").isPojo(), is(true));
		POJONode pojoNode = (POJONode) actualNode.path("metadata");
		assertThat(pojoNode.getPojo(), is(instanceOf(RawValue.class)));
		RawValue rawValue = (RawValue) pojoNode.getPojo();
		assertThat(rawValue.rawValue(), is("{\"foo\":\"bar\"}"));
		
		assertThat(actualNode.path("hoge_metadata").isPojo(), is(true));
		POJONode pojoNodeHoge = (POJONode) actualNode.path("hoge_metadata");
		assertThat(pojoNodeHoge.getPojo(), is(instanceOf(RawValue.class)));
		RawValue rawValueHoge = (RawValue) pojoNodeHoge.getPojo();
		assertThat(rawValueHoge.rawValue(), is("{\"baz\":\"qux\"}"));
		
		// exercise 2: Tree to Value
		Model actualValue = mapper.treeToValue(actualNode, Model.class);
		// verify 2
		assertThat(actualValue, is(model));
	}
	
	@Test
	public void testJsonToValue() throws Exception {
		// setup
		String modelJson = ""
				+ "{"
				+ "  'foo':'12341234',"
				+ "  'bar':1234,"
				+ "  'metadata':{"
				+ "    'foo':'bar'"
				+ "  },"
				+ "  'hoge_metadata':{"
				+ "    'baz':'qux'"
				+ "  }"
				+ "}";
		Model expected = new Model();
		expected.setFoo("12341234");
		expected.setBar(1234);
		expected.setMetadata("{\"foo\":\"bar\"}");
		expected.setHogeMetadata("{\"baz\":\"qux\"}");
		// exercise
		Model actual = mapper.readValue(modelJson, Model.class);
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testJsonToTreeToValue() throws Exception {
		// setup
		String modelJson = ""
				+ "{"
				+ "  'foo':'12341234',"
				+ "  'bar':1234,"
				+ "  'metadata':{"
				+ "    'foo':'bar'"
				+ "  },"
				+ "  'hoge_metadata':{"
				+ "    'baz':'qux'"
				+ "  }"
				+ "}";
		Model model = new Model();
		model.setFoo("12341234");
		model.setBar(1234);
		model.setMetadata("{\"foo\":\"bar\"}");
		model.setHogeMetadata("{\"baz\":\"qux\"}");
		
		// exercise 1: Json to Tree
		JsonNode actualNode = mapper.readTree(modelJson);
		// verify 1
		assertThat(actualNode.path("foo").isTextual(), is(true));
		assertThat(actualNode.path("foo").textValue(), is("12341234"));
		assertThat(actualNode.path("bar").isNumber(), is(true));
		assertThat(actualNode.path("bar").numberValue(), is(1234));
		
		// WARN: readTree does not serialize `metadata` as String, because mapper does not know this JSON is `Model`
		assertThat(actualNode.path("metadata").isObject(), is(true));
		assertThat(actualNode.path("metadata").path("foo").textValue(), is("bar"));
		
		assertThat(actualNode.path("hoge_metadata").isObject(), is(true));
		assertThat(actualNode.path("hoge_metadata").path("baz").textValue(), is("qux"));
		
		// exercise 2: Tree to Value
		Model actualValue = mapper.treeToValue(actualNode, Model.class);
		// verify 2
		assertThat(actualValue, is(model));
	}
}

@Data
@EqualsAndHashCode
class Model {
	
	private String foo;
	
	private int bar;
	
	@JsonRawValueExtended
	private String metadata;
	
	@JsonRawValueExtended
	private String hogeMetadata;
	
}
