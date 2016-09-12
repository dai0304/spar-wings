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
package jp.xet.sparwings.spring.data.chunk;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Test for {@link SimplePaginationTokenEncoder}.
 * 
 * @since 0.24
 * @version $Id$
 * @author daisuke
 */
@Slf4j
@SuppressWarnings("javadoc")
public class SimplePaginationTokenEncoderTest {
	
	SimplePaginationTokenEncoder sut = new SimplePaginationTokenEncoder();
	
	@Test
	public void testString() {
		String encoded = sut.encode("abc", "def");
		log.info(encoded);
		String first = sut.extractFirstKey(encoded).get();
		String last = sut.extractLastKey(encoded).get();
		assertThat(first, is("abc"));
		assertThat(last, is("def"));
	}
	
	@Test
	public void testInteger() {
		String encoded = sut.encode(123, 234);
		log.info(encoded);
		String first = sut.extractFirstKey(encoded).get();
		String last = sut.extractLastKey(encoded).get();
		assertThat(first, is("123"));
		assertThat(last, is("234"));
	}
	
	@Test
	public void testCompositeKey() {
		// setup
		CompositeKey first = new CompositeKey("a", 123L);
		CompositeKey last = new CompositeKey("z", 789L);
		// exercise
		String encoded = sut.encode(first, last);
		log.info(encoded);
		CompositeKey actualFirst = sut.extractFirstKey(encoded, CompositeKey.class).get();
		CompositeKey actualLast = sut.extractLastKey(encoded, CompositeKey.class).get();
		// verify
		assertThat(actualFirst, is(first));
		assertThat(actualLast, is(last));
	}
	
	@Data
	private static class CompositeKey {
		
		private final String key1;
		
		private final long key2;
		
		@JsonCreator
		public CompositeKey(@JsonProperty("key1") String key1, @JsonProperty("key2") long key2) {
			this.key1 = key1;
			this.key2 = key2;
		}
	}
}
