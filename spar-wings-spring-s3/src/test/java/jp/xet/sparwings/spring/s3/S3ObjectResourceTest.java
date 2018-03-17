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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Test for {@link S3ObjectResource}.
 * 
 * @since 0.12
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class S3ObjectResourceTest {
	
	@Mock
	AmazonS3 amazonS3;
	
	S3ObjectResource sut;
	
	
	@Test
	public void testSimpleConstruction() throws Exception {
		String bucketName = "bucket";
		String key = "path/to/foobar";
		// setup
		// exercise
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		// verify
		assertThat(sut.getDescription(), is("Amazon s3 resource [bucket='bucket', key='path/to/foobar']"));
	}
	
	@Test
	public void test_getFilename() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar.txt";
		String expected = "foobar.txt";
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		// exercise
		String actual = sut.getFilename();
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void test_getFilename_root() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "foobar.txt";
		String expected = "foobar.txt";
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		// exercise
		String actual = sut.getFilename();
		// verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void test_exists() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar.txt";
		boolean expected = true;
		when(amazonS3.getObjectMetadata(any(GetObjectMetadataRequest.class))).thenReturn(new ObjectMetadata());
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		// exercise
		boolean actual = sut.exists();
		// verify
		assertThat(actual, is(expected));
		ArgumentCaptor<GetObjectMetadataRequest> captor = ArgumentCaptor.forClass(GetObjectMetadataRequest.class);
		verify(amazonS3).getObjectMetadata(captor.capture());
		GetObjectMetadataRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(nullValue()));
	}
	
	@Test
	public void test_exists_withVersionId() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar.txt";
		String vid = "idididid";
		boolean expected = true;
		when(amazonS3.getObjectMetadata(any(GetObjectMetadataRequest.class))).thenReturn(new ObjectMetadata());
		sut = new S3ObjectResource(amazonS3, bucketName, key, vid);
		// exercise
		boolean actual = sut.exists();
		// verify
		assertThat(actual, is(expected));
		ArgumentCaptor<GetObjectMetadataRequest> captor = ArgumentCaptor.forClass(GetObjectMetadataRequest.class);
		verify(amazonS3).getObjectMetadata(captor.capture());
		GetObjectMetadataRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(vid));
	}
	
	@Test
	public void test_exists_notFound() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar.txt";
		boolean expected = false;
		AmazonS3Exception ex = new AmazonS3Exception("not found");
		ex.setStatusCode(404);
		when(amazonS3.getObjectMetadata(any(GetObjectMetadataRequest.class))).thenThrow(ex);
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		// exercise
		boolean actual = sut.exists();
		// verify
		assertThat(actual, is(expected));
		ArgumentCaptor<GetObjectMetadataRequest> captor = ArgumentCaptor.forClass(GetObjectMetadataRequest.class);
		verify(amazonS3).getObjectMetadata(captor.capture());
		GetObjectMetadataRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(nullValue()));
	}
	
	@Test
	public void test_contentLength() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar";
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		long expected = 123L;
		ObjectMetadata objectMetadata = mock(ObjectMetadata.class);
		when(objectMetadata.getContentLength()).thenReturn(expected);
		when(amazonS3.getObjectMetadata(any(GetObjectMetadataRequest.class))).thenReturn(objectMetadata);
		// exercise
		long actual = sut.contentLength();
		// verify
		assertThat(actual, is(expected));
		ArgumentCaptor<GetObjectMetadataRequest> captor = ArgumentCaptor.forClass(GetObjectMetadataRequest.class);
		verify(amazonS3).getObjectMetadata(captor.capture());
		GetObjectMetadataRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(nullValue()));
	}
	
	@Test
	@SuppressWarnings("resource")
	public void test_getInputStream() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar";
		sut = new S3ObjectResource(amazonS3, bucketName, key);
		S3ObjectInputStream expected = mock(S3ObjectInputStream.class);
		S3Object s3Object = mock(S3Object.class);
		when(s3Object.getObjectContent()).thenReturn(expected);
		when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
		// exercise
		InputStream actual = sut.getInputStream();
		// verify
		assertThat(actual, is(expected));
		verify(amazonS3).getObject(any());
		ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
		verify(amazonS3).getObject(captor.capture());
		GetObjectRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(nullValue()));
	}
	
	@Test
	@SuppressWarnings("resource")
	public void test_getInputStream_withVersionId() throws Exception {
		// setup
		String bucketName = "bucket";
		String key = "path/to/foobar";
		String vid = "idididid";
		sut = new S3ObjectResource(amazonS3, bucketName, key, vid);
		S3ObjectInputStream expected = mock(S3ObjectInputStream.class);
		S3Object s3Object = mock(S3Object.class);
		when(s3Object.getObjectContent()).thenReturn(expected);
		when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
		// exercise
		InputStream actual = sut.getInputStream();
		// verify
		assertThat(actual, is(expected));
		verify(amazonS3).getObject(any());
		ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
		verify(amazonS3).getObject(captor.capture());
		GetObjectRequest actualRequest = captor.getValue();
		assertThat(actualRequest.getBucketName(), is(bucketName));
		assertThat(actualRequest.getKey(), is(key));
		assertThat(actualRequest.getVersionId(), is(vid));
	}
}
