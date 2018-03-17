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
package jp.xet.sparwings.spring.s3;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.core.io.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test for {@link S3AssetsResolver}.
 *
 * @since 0.12
 * @author fd0
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class S3AssetsResolverTest {
	
	@Mock
	Resource resource;
	
	@Mock
	S3ObjectResourceLoader s3ObjectResourceLoader;
	
	@Test
	public void test_buildS3Uri() {
		// setup
		String requestPath = "bar.jpg";
		String expectedUri = "s3://assetsbucket/foo/bar.jpg";
		S3AssetsResolver sut = new S3AssetsResolver(null, "assetsbucket", "foo");
		// exercise
		String actual = sut.buildS3Uri(requestPath);
		// verify
		assertThat(actual, is(expectedUri));
	}
	
	@Test
	public void test_buildS3Uri_noPrefix() {
		// setup
		String requestPath = "bar.jpg";
		String expectedUri = "s3://assetsbucket/bar.jpg";
		S3AssetsResolver sut = new S3AssetsResolver(null, "assetsbucket", "");
		// exercise
		String actual = sut.buildS3Uri(requestPath);
		// verify
		assertThat(actual, is(expectedUri));
	}
	
	@Test
	public void test_buildS3Uri_subdir() {
		// setup
		String requestPath = "folder/bar.jpg";
		String expectedUri = "s3://assetsbucket/folder/bar.jpg";
		S3AssetsResolver sut = new S3AssetsResolver(null, "assetsbucket", "");
		// exercise
		String actual = sut.buildS3Uri(requestPath);
		// verify
		assertThat(actual, is(expectedUri));
	}
	
	@Test
	public void test_resolveResourceInternal() {
		// setup
		when(resource.exists()).thenReturn(true);
		when(s3ObjectResourceLoader.getResource(any(String.class))).thenReturn(resource);
		S3AssetsResolver sut = new S3AssetsResolver(s3ObjectResourceLoader, "assetsbucket", "foo");
		// exercise
		Resource actual = sut.resolveResourceInternal("");
		// verify
		assertThat(actual, is(resource));
	}
	
	@Test
	public void test_resolveResourceInternal_nonexistent() {
		// setup
		when(resource.exists()).thenReturn(false);
		when(s3ObjectResourceLoader.getResource(any(String.class))).thenReturn(resource);
		S3AssetsResolver sut = new S3AssetsResolver(s3ObjectResourceLoader, "assetsbucket", "foo");
		// exercise
		Resource actual = sut.resolveResourceInternal("");
		// verify
		assertNull(actual);
	}
}
