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
package jp.xet.sparwings.spring.security.passwordencoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * Test for {@link VersionedPasswordEncoder}.
 * 
 * @since #version#
 */
@SuppressWarnings("javadoc")
public class VersionedPasswordEncoderTest {
	
	Map<String, PasswordEncoder> legalMap = ImmutableMap.<String, PasswordEncoder> builder()
		.put("foo", NoOpPasswordEncoder.getInstance())
		.put("bar", new StandardPasswordEncoder())
		.put("baz", new BCryptPasswordEncoder())
		.build();
	
	Map<String, PasswordEncoder> illegalMap = ImmutableMap.<String, PasswordEncoder> builder()
		.put("foo-bar", NoOpPasswordEncoder.getInstance())
		.put("bar", new StandardPasswordEncoder())
		.build();
	
	VersionedPasswordEncoder sut = new VersionedPasswordEncoder("foo", "baz", legalMap);
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstruct_primaryEncoder_notFound() {
		new VersionedPasswordEncoder("foo", "xxx", legalMap);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstruct_unknownEncoder_notFound() {
		new VersionedPasswordEncoder("xxx", "bar", legalMap);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstruct_keyPattern_doesNotMet() {
		new VersionedPasswordEncoder("bar", "foo-bar", illegalMap);
	}
	
	@Test
	public void testEncode() {
		// setup
		String rawPassword = "p@ssW0rd";
		// exercise
		String actual = sut.encode(rawPassword);
		// verify
		assertThat(actual, startsWith("baz_"));
	}
	
	@Test
	public void testMatchesEncodedWithKey1() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "baz_$2a$10$cuR2ghEiftvcxKx/V8ZNieZBs4Xfiq6ZsRapxHZRnfWpMyBwQYUme"; // new BCryptPasswordEncoder().encode(rawPassword);
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesEncodedWithKey2() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "foo_p@ssW0rd";
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesEncodedWithKey3() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "bar_9bf18ae42422e047a5fa740bc9a41110d0146c5cf2b8ba6df1cc583e23202adbd132f9cb3da6e9c1"; // new StandardPasswordEncoder().encode(rawPassword);
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesEncodedWithoutKey() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "p@ssW0rd";
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesAutoDetection_STANDARD() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "9bf18ae42422e047a5fa740bc9a41110d0146c5cf2b8ba6df1cc583e23202adbd132f9cb3da6e9c1";
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesAutoDetection_BCRYPT() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "$2a$10$cuR2ghEiftvcxKx/V8ZNieZBs4Xfiq6ZsRapxHZRnfWpMyBwQYUme";
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertTrue(actual);
	}
	
	@Test
	public void testMatchesAutoDetectionDisabled_STANDARD() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "9bf18ae42422e047a5fa740bc9a41110d0146c5cf2b8ba6df1cc583e23202adbd132f9cb3da6e9c1";
		sut.setEnableAutoDetection(false);
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertFalse(actual);
	}
	
	@Test
	public void testMatchesAutoDetectionDisabled_BCRYPT() throws Exception {
		// setup
		String rawPassword = "p@ssW0rd";
		String encodedPassword = "$2a$10$cuR2ghEiftvcxKx/V8ZNieZBs4Xfiq6ZsRapxHZRnfWpMyBwQYUme";
		sut.setEnableAutoDetection(false);
		// exercise
		boolean actual = sut.matches(rawPassword, encodedPassword);
		// verify
		assertFalse(actual);
	}
}
