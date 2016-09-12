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
package jp.xet.sparwings.spring.data.model;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import lombok.Data;

/**
 * Test for {@link Resource} serialization.
 * 
 * @since 0.22
 * @version $Id$
 * @author daisuke
 */
public class ResourceTest {
	
	private static final ObjectMapper OM = new ObjectMapper();
	static {
		OM.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	@Test
	public void testStringSer() throws Exception {
		Resource<String> fooResource = new Resource<>("foo");
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.value", is("foo")));
	}
	
	@Test
	public void testIntegerSer() throws Exception {
		Resource<Integer> fooResource = new Resource<>(123);
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.value", is(123)));
	}
	
	@Test
	public void testBeanSer() throws Exception {
		Resource<SampleBean> fooResource = new Resource<>(new SampleBean("aaa", "bbb"));
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.foo", is("aaa")));
		assertThat(actual, hasJsonPath("$.bar", is("bbb")));
	}
	
	@Test
	public void testBeanDeser() throws Exception {
		Resource<SampleBean> expected = new Resource<>(new SampleBean("aaa", "bbb"));
		expected.embedResource("rel", "embedded-value");
		expected.add("self", new Link("http://example.com/self"));
		String json = "{"
				+ "  'foo': 'aaa',"
				+ "  'bar': 'bbb',"
				+ "  '_embedded': {"
				+ "    'rel': 'embedded-value'"
				+ "  },"
				+ "  '_links': {"
				+ "    'self': { 'href': 'http://example.com/self' }"
				+ "  }"
				+ "}";
		Resource<SampleBean> actual = OM.readValue(json, new TypeReference<Resource<SampleBean>>() {
		});
		assertThat(actual, is(expected));
	}
	
	@Data
	@SuppressWarnings("javadoc")
	public static class SampleBean {
		
		private String foo;
		
		private String bar;
		
		@JsonCreator
		public SampleBean(@JsonProperty("foo") String foo, @JsonProperty("bar") String bar) {
			this.foo = foo;
			this.bar = bar;
		}
	}
}
