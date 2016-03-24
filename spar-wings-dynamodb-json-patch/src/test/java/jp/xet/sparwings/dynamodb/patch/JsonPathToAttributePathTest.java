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

import com.github.fge.jackson.jsonpointer.JsonPointer;

import org.junit.Test;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class JsonPathToAttributePathTest {
	
	JsonPathToAttributePath sut = new JsonPathToAttributePath();
	
	
	@Test
	public void test() throws Exception {
		// setup
		JsonPointer pointer = new JsonPointer("/a/b/c");
		String expected = "a.b.c";
		// exercise
		String actual = sut.apply(pointer);
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testFoo() throws Exception {
		// setup
		JsonPointer pointer = new JsonPointer("/foo");
		String expected = "foo";
		// exercise
		String actual = sut.apply(pointer);
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testFoo0() throws Exception {
		// setup
		JsonPointer pointer = new JsonPointer("/foo/0");
		String expected = "foo[0]";
		// exercise
		String actual = sut.apply(pointer);
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testEmpty() throws Exception {
		// setup
		JsonPointer pointer = new JsonPointer("/");
		String expected = "";
		// exercise
		String actual = sut.apply(pointer);
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testSlash() throws Exception {
		// setup
		JsonPointer pointer = new JsonPointer("/a~1b");
		String expected = "a/b";
		// exercise
		String actual = sut.apply(pointer);
		// verify
		assertThat(actual, is(expected));
	}
}
