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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	static {
		OM.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	private static final PaginationTokenEncoder ENCODER = new SimplePaginationTokenEncoder();
	
	
	@Test
	public void testStringsSer() throws Exception {
		// setup
		String paginationToken = ENCODER.encode("aaa", "ccc");
		List<String> content = Arrays.asList("aaa", "bbb", "ccc");
		Chunk<String> chunk = new ChunkImpl<>(content, paginationToken, new ChunkRequest(10));
		ChunkedResources<String> stringsChunkResource = new ChunkedResources<>("strings", chunk);
		// exercise
		String actual = OM.writeValueAsString(stringsChunkResource);
		// verify
		assertThat(actual, hasJsonPath("$._embedded.strings[0]", is("aaa")));
		assertThat(actual, hasJsonPath("$._embedded.strings[1]", is("bbb")));
		assertThat(actual, hasJsonPath("$._embedded.strings[2]", is("ccc")));
		assertThat(actual, hasJsonPath("$.chunk.size", is(chunk.getContent().size())));
		assertThat(actual, hasJsonPath("$.chunk.pagination_token", is(chunk.getPaginationToken())));
	}
	
	@Test
	public void testBeanSer() throws Exception {
		// setup
		String paginationToken = ENCODER.encode("aaa", "ccc");
		List<SampleBean> content = Arrays.asList(new SampleBean("aaa", "bbb"), new SampleBean("ccc", "ddd"));
		Chunk<SampleBean> chunk = new ChunkImpl<>(content, paginationToken, new ChunkRequest(10));
		ChunkedResources<SampleBean> stringsChunkResource = new ChunkedResources<>("beans", chunk);
		// exercise
		String actual = OM.writeValueAsString(stringsChunkResource);
		// verify
		assertThat(actual, hasJsonPath("$._embedded.beans[0].foo", is("aaa")));
		assertThat(actual, hasJsonPath("$._embedded.beans[0].bar", is("bbb")));
		assertThat(actual, hasJsonPath("$._embedded.beans[1].foo", is("ccc")));
		assertThat(actual, hasJsonPath("$._embedded.beans[1].bar", is("ddd")));
	}
	
	@Test
	public void testBeanDeser() throws Exception {
		String paginationToken = ENCODER.encode("aaa", "ccc");
		List<SampleBean> content = Arrays.asList(new SampleBean("aaa", "bbb"), new SampleBean("ccc", "ddd"));
		Chunk<SampleBean> chunk = new ChunkImpl<>(content, paginationToken, new ChunkRequest(10));
		ChunkedResources<Resource<SampleBean>> expected =
				new ChunkedResources<>("beans", chunk, Resource<SampleBean>::new);
		String json = "{\n"
				+ "  'chunk': {\n"
				+ "    'size': 2,\n"
				+ "    'pagination_token': '" + paginationToken + "'\n"
				+ "  },\n"
				+ "  '_embedded': {\n"
				+ "    'beans': [\n"
				+ "      { 'foo': 'aaa', 'bar': 'bbb' },\n"
				+ "      { 'foo': 'ccc', 'bar': 'ddd' }\n"
				+ "    ]\n"
				+ "  }\n"
				+ "}";
		// exercis
		ChunkedResources<? extends Resource<SampleBean>> actual = OM.readValue(json,
				new TypeReference<ChunkedResources<? extends Resource<SampleBean>>>() {
				});
		// verify
		assertThat(actual.getMetadata(), is(expected.getMetadata()));
		assertThat(actual.getContent(), is(expected.getContent()));
		assertThat(actual.getContent().get("beans"), is(expected.getContent().get("beans")));
		
		Collection<? extends Resource<SampleBean>> resources = actual.getContent().get("beans");
		Iterator<? extends Resource<SampleBean>> itr = resources.iterator();
		Resource<SampleBean> res1 = itr.next();
		assertThat(res1.getValue().getFoo(), is("aaa"));
		assertThat(res1.getValue().getBar(), is("bbb"));
		Resource<SampleBean> res2 = itr.next();
		assertThat(res2.getValue().getFoo(), is("ccc"));
		assertThat(res2.getValue().getBar(), is("ddd"));
	}
	
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuppressWarnings("javadoc")
	public static class SampleBean {
		
		private String foo;
		
		private String bar;
	}
}
