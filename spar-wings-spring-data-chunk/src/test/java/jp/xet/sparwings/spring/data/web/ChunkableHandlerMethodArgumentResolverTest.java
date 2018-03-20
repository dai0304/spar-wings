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
package jp.xet.sparwings.spring.data.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import org.junit.Test;

import jp.xet.sparwings.spring.data.chunk.Chunkable;
import jp.xet.sparwings.spring.data.chunk.Chunkable.PaginationRelation;

/**
 * Test for {@link ChunkableHandlerMethodArgumentResolver}.
 * 
 * @since 0.19
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
public class ChunkableHandlerMethodArgumentResolverTest {
	
	ChunkableHandlerMethodArgumentResolver sut = new ChunkableHandlerMethodArgumentResolver();
	
	
	public void simpleHandler(Chunkable chunkable) {
		// nothing to do
	}
	
	@Test
	public void testSimpleHandler() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		assertThat(actual, is(sameInstance(sut.getFallbackChunkable())));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	public void defaultHandler(@ChunkableDefault Chunkable chunkable) {
		// nothing to do
	}
	
	@Test
	public void testDefaultHandler() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(10));
		assertThat(actualChunkable.getDirection(), is(Direction.ASC));
	}
	
	public void defaultHandlerWithValue(@ChunkableDefault(size = 123) Chunkable chunkable) {
		// nothing to do
	}
	
	@Test
	public void testDefaultHandlerWithValue() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandlerWithValue", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(123));
		assertThat(actualChunkable.getDirection(), is(Direction.ASC));
	}
	
	public void multipleChunkable(Chunkable c1, Chunkable c2) {
		// nothing to do
	}
	
	@Test(expected = IllegalStateException.class)
	public void testMultipleChunkable() throws Exception {
		// setup
		Method method = getClass().getMethod("multipleChunkable", Chunkable.class, Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// assert exception
	}
	
	@Test
	public void testSimpleHandlerWithSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("123");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(123));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDefaultHandlerWithSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("123");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(123));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDefaultHandlerWithValueWithSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandlerWithValue", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("123");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(123));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testSimpleHandlerWithExceededSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("12345");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDefaultHandlerWithExceededSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("12345");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testNegativeSize() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("-1");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(1));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testSimpleHandlerWithIllegalSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("foo");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDefaultHandlerWithIllegalSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("foo");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(10));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDefaultHandlerWithValueWithIllegalSizeParameter() throws Exception {
		// setup
		Method method = getClass().getMethod("defaultHandlerWithValue", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("foo");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(123));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testAfter() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("next"))).thenReturn("100");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(actualChunkable.getPaginationToken(), is(notNullValue())); // TODO assert last=100
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testBefore() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("prev"))).thenReturn("89");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(PaginationRelation.PREV));
		assertThat(actualChunkable.getPaginationToken(), is(notNullValue())); // TODO assert first=89
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testAfterAndSize() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("10");
		when(webRequest.getParameter(eq("next"))).thenReturn("100");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(actualChunkable.getPaginationToken(), is(notNullValue())); // TODO assert first=100
		assertThat(actualChunkable.getMaxPageSize(), is(10));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testDirection() throws Exception {
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("direction"))).thenReturn("DESC");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(Direction.DESC));
	}
	
	@Test
	public void testIllegalDirection() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("direction"))).thenReturn("FOO");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(nullValue()));
		assertThat(actualChunkable.getPaginationToken(), is(nullValue()));
		assertThat(actualChunkable.getMaxPageSize(), is(2000));
		assertThat(actualChunkable.getDirection(), is(nullValue()));
	}
	
	@Test
	public void testFull() throws Exception {
		// setup
		Method method = getClass().getMethod("simpleHandler", Chunkable.class);
		MethodParameter methodParametere = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		when(webRequest.getParameter(eq("size"))).thenReturn("10");
		when(webRequest.getParameter(eq("next"))).thenReturn("89");
		when(webRequest.getParameter(eq("prev"))).thenReturn("100"); // ignored
		when(webRequest.getParameter(eq("direction"))).thenReturn("DESC");
		WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);
		// exercise
		Object actual = sut.resolveArgument(methodParametere, mavContainer, webRequest, binderFactory);
		// verify
		assertThat(actual, is(notNullValue()));
		assertThat(actual, is(instanceOf(Chunkable.class)));
		Chunkable actualChunkable = (Chunkable) actual;
		
		assertThat(actualChunkable.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(actualChunkable.getPaginationToken(), is(notNullValue())); // TODO assert first=89
		assertThat(actualChunkable.getMaxPageSize(), is(10));
		assertThat(actualChunkable.getDirection(), is(Direction.DESC));
	}
}
