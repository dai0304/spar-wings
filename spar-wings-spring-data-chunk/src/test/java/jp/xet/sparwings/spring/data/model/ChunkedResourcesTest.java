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
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import jp.xet.sparwings.spring.data.chunk.ChunkImpl;
import jp.xet.sparwings.spring.data.chunk.ChunkRequest;
import jp.xet.sparwings.spring.data.chunk.PaginationTokenEncoder;
import jp.xet.sparwings.spring.data.chunk.SimplePaginationTokenEncoder;

/**
 * TODO for daisuke
 * 
 * @since 0.22
 * @version $Id$
 * @author daisuke
 */
public class ChunkedResourcesTest {
	
	private static final ObjectMapper OM = new ObjectMapper();
	
	private static final PaginationTokenEncoder ENCODER = new SimplePaginationTokenEncoder();
	
	
	@Test
	public void testStrings() throws Exception {
		String paginationToken = ENCODER.encode("aaa", "ccc");
		List<String> content = Arrays.asList("aaa", "bbb", "ccc");
		Chunk<String> chunk = new ChunkImpl<>(content, paginationToken, new ChunkRequest(10));
		ChunkedResources<String> stringsChunkResource = new ChunkedResources<>("strings", chunk);
		String actual = OM.writeValueAsString(stringsChunkResource);
		assertThat(actual, hasJsonPath("$._embedded.strings[0]", is("aaa")));
		assertThat(actual, hasJsonPath("$._embedded.strings[1]", is("bbb")));
		assertThat(actual, hasJsonPath("$._embedded.strings[2]", is("ccc")));
		assertThat(actual, hasJsonPath("$.chunk.size", is(chunk.getContent().size())));
		assertThat(actual, hasJsonPath("$.chunk.pagination_token", is(chunk.getPaginationToken())));
	}
	
}
