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

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test for {@link PagedResources} serialization.
 * 
 * @since 0.22
 * @version $Id$
 * @author daisuke
 */
public class PagedResourcesTest {
	
	private static final ObjectMapper OM = new ObjectMapper();
	
	
	@Test
	public void testStrings() throws Exception {
		Page<String> page = new PageImpl<>(Arrays.asList("foo", "bar", "baz"));
		PagedResources<String> stringsPageResource = new PagedResources<>("strings", page);
		String actual = OM.writeValueAsString(stringsPageResource);
		assertThat(actual, hasJsonPath("$._embedded.strings[0]", is("foo")));
		assertThat(actual, hasJsonPath("$._embedded.strings[1]", is("bar")));
		assertThat(actual, hasJsonPath("$._embedded.strings[2]", is("baz")));
		assertThat(actual, hasJsonPath("$.page.size", is(page.getSize())));
		assertThat(actual, hasJsonPath("$.page.total_pages", is(page.getTotalPages())));
		assertThat(actual, hasJsonPath("$.page.number", is(page.getNumber())));
		assertThat(actual, hasJsonPath("$.page.total_elements", is(Long.valueOf(page.getTotalElements()).intValue())));
	}
	
}
