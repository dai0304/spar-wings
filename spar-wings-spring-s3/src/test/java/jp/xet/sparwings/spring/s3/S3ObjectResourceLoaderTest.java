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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Test for {@link S3ObjectResourceLoader}.
 * 
 * @since 0.12
 * @author daisuke
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class S3ObjectResourceLoaderTest {
	
	@Mock
	AmazonS3 amazonS3;
	
	@Mock
	ResourceLoader delegate;
	
	@InjectMocks
	S3ObjectResourceLoader sut;
	
	
	@Before
	public void setup() throws Exception {
		sut.afterPropertiesSet();
	}
	
	@Test
	public void test_getResource() {
		String location = "s3://bucket/path/to/foobar";
		String expectedDescription = "Amazon s3 resource [bucket='bucket', key='path/to/foobar']";
		// exercise
		Resource actual = sut.getResource(location);
		// verify
		assertThat(actual.getDescription(), is(expectedDescription));
		verify(delegate, never()).getResource(anyString());
	}
	
	@Test
	public void test_getResourceWithVersion() {
		String location = "s3://bucket/path/to/foobar?versionId=bazqux";
		String expectedDescription = "Amazon s3 resource [bucket='bucket', key='path/to/foobar', versionId='bazqux']";
		// exercise
		Resource actual = sut.getResource(location);
		// verify
		assertThat(actual.getDescription(), is(expectedDescription));
		verify(delegate, never()).getResource(anyString());
	}
}
