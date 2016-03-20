package jp.xet.sparwings.spring.s3;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

/**
 * Test for {@link S3AssetsResolver}.
 * 
 * @since #version#
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
