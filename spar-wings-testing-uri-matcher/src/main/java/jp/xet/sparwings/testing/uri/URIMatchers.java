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
package jp.xet.sparwings.testing.uri;

import static org.hamcrest.Matchers.is;

import java.net.URI;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * JUnit matchers to assert {@link URI}s.
 * 
 * @since 0.15
 * @version $Id$
 * @author daisuke
 */
public class URIMatchers {
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s scheme matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com"), hasScheme(startsWith("http")))</pre>
	 * </p>
	 * 
	 * @param matcher The scheme string matcher
	 * @return The scheme matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasScheme(Matcher<String> matcher) {
		return new HasSchema(matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} have specified scheme.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("http://example.com"), hasScheme("http"))</pre>
	 * </p>
	 * 
	 * @param scheme The scheme string
	 * @return The scheme matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasScheme(String scheme) {
		return new HasSchema(is(scheme));
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s authority matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com:8080"), hasAuthority(containsString(":")))</pre>
	 * </p>
	 * 
	 * @param matcher The authority string matcher
	 * @return The authority matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasAuthority(Matcher<String> matcher) {
		return new HasAuthority(matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} have specified authority.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("http://example.com:8080"), hasAuthority("example.com:8080"))</pre>
	 * </p>
	 * 
	 * @param authority The authority string
	 * @return The authority matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasAuthority(String authority) {
		return new HasAuthority(is(authority));
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s hostname matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com:8080"), hasHost(endsWith(".com")))</pre>
	 * </p>
	 * 
	 * @param matcher The hostname string matcher
	 * @return The hostname matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasHost(Matcher<String> matcher) {
		return new HasHost(matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} have specified hostname.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("http://example.com:8080"), hasHost("example.com"))</pre>
	 * </p>
	 * 
	 * @param host The hostname string
	 * @return The hostname matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasHost(String host) {
		return new HasHost(is(host));
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s port number matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com:8080"), hasPort(greaterThan(1000)))</pre>
	 * </p>
	 * 
	 * @param matcher The port number matcher
	 * @return The port number matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasPort(Matcher<Integer> matcher) {
		return new HasPort(matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} have specified port.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("http://example.com:8080"), hasPort(8080))</pre>
	 * </p>
	 * 
	 * @param port The port number
	 * @return The port number matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasPort(int port) {
		return new HasPort(is(port));
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s path matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com/foo/bar"), hasPath(startsWitsh("/foo")))</pre>
	 * </p>
	 * 
	 * @param matcher The path matcher
	 * @return The path matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasPath(Matcher<String> matcher) {
		return new HasPath(matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} have specified path.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("http://example.com/foo/bar"), hasPath("/foo/bar"))</pre>
	 * </p>
	 * 
	 * @param path The path
	 * @return The path matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasPath(String path) {
		return new HasPath(is(path));
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} has specified name query parameter.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com/?foo=bar"), hasQueryParam("foo"))</pre>
	 * </p>
	 * 
	 * @param key The query parameter name
	 * @return The query parameter matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasQueryParam(String key) {
		return new HasQueryParam(key, null);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s specific name query parameter value matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com/?foo=bar123"), hasQueryParam("foo", startsWitsh("bar")))</pre>
	 * </p>
	 * 
	 * @param key The query parameter name
	 * @param matcher The value matcher
	 * @return The query parameter matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasQueryParam(String key, Matcher<String> matcher) {
		return new HasQueryParam(key, matcher);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI} has specified name fragment parameter.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com/#foo=bar"), hasFragmentParam("foo"))</pre>
	 * </p>
	 * 
	 * @param key The fragment parameter name
	 * @return The fragment parameter matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasFragmentParam(String key) {
		return new HasFragmentParam(key, null);
	}
	
	/**
	 * Creates a matcher for {@link URI}s that matches if the examined {@link URI}'s specific name fragment parameter value matches specified matcher.
	 * 
	 * <p>
	 * For example:
	 * <pre>assertThat(new URI("https://example.com/#foo=bar123"), hasFragmentParam("foo", startsWitsh("bar")))</pre>
	 * </p>
	 * 
	 * @param key The fragment parameter name
	 * @param matcher The value matcher
	 * @return The fragment parameter matcher
	 * @since 0.15
	 */
	@Factory
	public static Matcher<URI> hasFragmentParam(String key, Matcher<String> matcher) {
		return new HasFragmentParam(key, matcher);
	}
}
