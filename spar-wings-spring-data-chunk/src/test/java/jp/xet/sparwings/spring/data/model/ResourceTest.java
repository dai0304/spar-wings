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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO for daisuke
 * 
 * @since 0.22
 * @version $Id$
 * @author daisuke
 */
public class ResourceTest {
	
	private static final ObjectMapper OM = new ObjectMapper();
	
	
	@Test
	public void testString() throws Exception {
		Resource<String> fooResource = new Resource<>("foo");
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.value", is("foo")));
	}
	
	@Test
	public void testInteger() throws Exception {
		Resource<Integer> fooResource = new Resource<>(123);
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.value", is(123)));
	}
	
	@Test
	public void testBean() throws Exception {
		Resource<SampleBean> fooResource = new Resource<>(new SampleBean("aaa", "bbb"));
		String actual = OM.writeValueAsString(fooResource);
		assertThat(actual, hasJsonPath("$.foo", is("aaa")));
		assertThat(actual, hasJsonPath("$.bar", is("bbb")));
	}
	
	
	@Data
	@AllArgsConstructor
	@SuppressWarnings("javadoc")
	public static class SampleBean {
		
		private String foo;
		
		private String bar;
	}
}
