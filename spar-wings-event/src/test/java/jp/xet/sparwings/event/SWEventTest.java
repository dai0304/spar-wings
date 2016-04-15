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
package jp.xet.sparwings.event;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.InputStreamReader;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;

import org.junit.Test;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@Slf4j
@SuppressWarnings("javadoc")
public class SWEventTest {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	
	@Test
	public void testFullSerialize() throws Exception {
		// setup
		EventSourceDescriptor source = new EventSourceDescriptor()
			.setEnvironment(new EnvironmentDescriptor()
				.setAccountId("000011112222")
				.setRegion("ap-northeast-1")
				.setInstanceId("i-0123456789abcdef"))
			.setProduct(new ProductDescriptor()
				.setName("foobar")
				.setRole("bazqux")
				.setVersion("1.23"));
		SWEvent sut = new SWEvent(source, "test_event")
			.setAuthentication(new AuthenticationDescriptor()
				.setClientId("test.client01")
				.setUsername("test.user01"))
			.setHttpRequest(new HttpRequestDescriptor()
				.setRequestId("57e4e60b-fd57-402f-a5f2-bc984fa63c16")
				.setUri("http://exmample.com/foo/bar")
				.setMethod("GET")
				.setRemoteAddr("203.0.113.3")
				.setRemoteHost("203.0.113.3")
				.setSessionId("4c0f6cbf-173a-4bef-af7a-20919954133b"))
			.setQueueMessage(new QueueMessageDescriptor()
				.setQueue("some-queue")
				.setMessageId("2c71e5ce-e0bc-4d89-94b8-65a1e17126fb"))
			.with("foo", "some string")
			.with("bar", 123)
			.with("baz", true)
			.with("qux", null)
			.with("quux", Arrays.asList(1, 2, 3))
			.with("corge", Arrays.asList("a", "bb", "ccc"))
			.with("grault", Arrays.asList("s", false, 12))
			.with("garply", ImmutableMap.of("a", "abc", "b", true, "c", 23));
		log.info("ToString: {}", sut.toString());
		
		// exercise
		String actual = MAPPER.writeValueAsString(sut);
		
		// verify
		log.info("ToJson: {}", actual);
		
		assertThat(actual, isJson());
		
		assertThat(actual, hasJsonPath("$.timestamp"));
		assertThat(actual, hasJsonPath("$.event_type", is("test_event")));
		
		assertThat(actual, hasJsonPath("$.source.environment.acoount_id", is("000011112222")));
		assertThat(actual, hasJsonPath("$.source.environment.region", is("ap-northeast-1")));
		assertThat(actual, hasJsonPath("$.source.environment.instance_id", is("i-0123456789abcdef")));
		assertThat(actual, hasJsonPath("$.source.product.name", is("foobar")));
		assertThat(actual, hasJsonPath("$.source.product.role", is("bazqux")));
		assertThat(actual, hasJsonPath("$.source.product.version", is("1.23")));
		
		assertThat(actual, hasJsonPath("$.authentication.client_id", is("test.client01")));
		assertThat(actual, hasJsonPath("$.authentication.username", is("test.user01")));
		
		assertThat(actual, hasJsonPath("$.request.request_id", is("57e4e60b-fd57-402f-a5f2-bc984fa63c16")));
		assertThat(actual, hasJsonPath("$.request.uri", is("http://exmample.com/foo/bar")));
		assertThat(actual, hasJsonPath("$.request.method", is("GET")));
		assertThat(actual, hasJsonPath("$.request.remote_addr", is("203.0.113.3")));
		assertThat(actual, hasJsonPath("$.request.remote_host", is("203.0.113.3")));
		assertThat(actual, hasJsonPath("$.request.session_id", is("4c0f6cbf-173a-4bef-af7a-20919954133b")));
		
		assertThat(actual, hasJsonPath("$.message.queue", is("some-queue")));
		assertThat(actual, hasJsonPath("$.message.message_id", is("2c71e5ce-e0bc-4d89-94b8-65a1e17126fb")));
		
		assertThat(actual, hasJsonPath("$.foo", is("some string")));
		assertThat(actual, hasJsonPath("$.bar", is(123)));
		assertThat(actual, hasJsonPath("$.baz", is(true)));
		assertThat(actual, hasJsonPath("$.qux", is(nullValue())));
		assertThat(actual, hasJsonPath("$.quux", hasSize(3)));
		assertThat(actual, hasJsonPath("$.quux", hasItems(1, 2, 3)));
		assertThat(actual, hasJsonPath("$.corge", hasSize(3)));
		assertThat(actual, hasJsonPath("$.corge[0]", is("a")));
		assertThat(actual, hasJsonPath("$.corge[1]", is("bb")));
		assertThat(actual, hasJsonPath("$.corge[2]", is("ccc")));
		assertThat(actual, hasJsonPath("$.grault", hasSize(3)));
		assertThat(actual, hasJsonPath("$.grault[0]", is("s")));
		assertThat(actual, hasJsonPath("$.grault[1]", is(false)));
		assertThat(actual, hasJsonPath("$.grault[2]", is(12)));
		assertThat(actual, hasJsonPath("$.garply.a", is("abc")));
		assertThat(actual, hasJsonPath("$.garply.b", is(true)));
		assertThat(actual, hasJsonPath("$.garply.c", is(23)));
	}
	
	@Test
	public void testFullDeserialize() throws Exception {
		// setup
		String json = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("/sample-event.json")));
		EventSourceDescriptor source = new EventSourceDescriptor()
			.setEnvironment(new EnvironmentDescriptor()
				.setAccountId("000011112222")
				.setRegion("ap-northeast-1")
				.setInstanceId("i-0123456789abcdef"))
			.setProduct(new ProductDescriptor()
				.setName("foobar")
				.setRole("bazqux")
				.setVersion("1.23"));
		SWEvent expected = new SWEvent(source, 1460622200299L, "test_event")
			.setAuthentication(new AuthenticationDescriptor()
				.setClientId("test.client01")
				.setUsername("test.user01"))
			.setHttpRequest(new HttpRequestDescriptor()
				.setRequestId("57e4e60b-fd57-402f-a5f2-bc984fa63c16")
				.setUri("http://exmample.com/foo/bar")
				.setMethod("GET")
				.setRemoteAddr("203.0.113.3")
				.setRemoteHost("203.0.113.3")
				.setSessionId("4c0f6cbf-173a-4bef-af7a-20919954133b"))
			.setQueueMessage(new QueueMessageDescriptor()
				.setQueue("some-queue")
				.setMessageId("2c71e5ce-e0bc-4d89-94b8-65a1e17126fb"))
			.with("foo", "some string")
			.with("bar", 123)
			.with("baz", true)
			.with("qux", null);
		
		// exercise
		SWEvent actual = MAPPER.readValue(json, SWEvent.class);
		
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testMinimumSerialize() throws Exception {
		EventSourceDescriptor source = new EventSourceDescriptor()
			.setProduct(new ProductDescriptor()
				.setName("foobar")
				.setRole("bazqux")
				.setVersion("1.23"));
		
		SWEvent sut = new SWEvent(source, "test_event2")
			.setHttpRequest(new HttpRequestDescriptor()
				.setRequestId("57e4e60b-fd57-402f-a5f2-bc984fa63c16")
				.setUri("http://exmample.com/foo/bar")
				.setMethod("GET")
				.setRemoteAddr("203.0.113.3")
				.setRemoteHost("203.0.113.3"));
		log.info("ToString: {}", sut.toString());
		
		// exercise
		String actual = MAPPER.writeValueAsString(sut);
		
		// verify
		log.info("ToJson: {}", actual);
		
		assertThat(actual, isJson());
		
		assertThat(actual, hasJsonPath("$.timestamp"));
		assertThat(actual, hasJsonPath("$.event_type", is("test_event2")));
		
		assertThat(actual, hasJsonPath("$.source.product.name", is("foobar")));
		assertThat(actual, hasJsonPath("$.source.product.role", is("bazqux")));
		assertThat(actual, hasJsonPath("$.source.product.version", is("1.23")));
		
		assertThat(actual, hasJsonPath("$.request.request_id", is("57e4e60b-fd57-402f-a5f2-bc984fa63c16")));
		assertThat(actual, hasJsonPath("$.request.uri", is("http://exmample.com/foo/bar")));
		assertThat(actual, hasJsonPath("$.request.method", is("GET")));
		assertThat(actual, hasJsonPath("$.request.remote_addr", is("203.0.113.3")));
		assertThat(actual, hasJsonPath("$.request.remote_host", is("203.0.113.3")));
	}
}
