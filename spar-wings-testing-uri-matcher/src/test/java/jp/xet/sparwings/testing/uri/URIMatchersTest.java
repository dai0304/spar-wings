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

import static jp.xet.sparwings.testing.uri.URIMatchers.hasAuthority;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasFragmentParam;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasHost;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasPath;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasPort;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasQueryParam;
import static jp.xet.sparwings.testing.uri.URIMatchers.hasScheme;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

/**
 * Test for {@link URIMatchers}.
 * 
 * @since 0.15
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
public class URIMatchersTest {
	
	final URI uri;
	
	{
		try {
			uri = new URI("http://user:pass@example.com:8080/foo?bar=baz&qux=quux#courge=grault&garply=waldo");
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
	
	@Test
	public void testHasScheme() throws Exception {
		assertThat(uri, hasScheme("http"));
		assertThat(uri, not(hasScheme("https")));
	}
	
	@Test
	public void testHasAuthority() throws Exception {
		assertThat(uri, hasAuthority("user:pass@example.com:8080"));
		assertThat(uri, not(hasAuthority("user:pass@example.com:80")));
	}
	
	@Test
	public void testHasHost() throws Exception {
		assertThat(uri, hasHost("example.com"));
		assertThat(uri, not(hasHost("foo.example.com")));
	}
	
	@Test
	public void testHasPort() throws Exception {
		assertThat(uri, hasPort(8080));
		assertThat(uri, hasPort(lessThan(10000)));
		assertThat(uri, hasPort(greaterThan(1000)));
		assertThat(uri, not(hasPort(80)));
	}
	
	@Test
	public void testHasPath() throws Exception {
		assertThat(uri, hasPath("/foo"));
		assertThat(uri, hasPath(startsWith("/")));
		assertThat(uri, not(hasPath(startsWith("/hoge"))));
	}
	
	@Test
	public void testHasQueryParam() throws Exception {
		assertThat(uri, hasQueryParam("bar"));
		assertThat(uri, not(hasQueryParam("baz")));
		assertThat(uri, hasQueryParam("qux"));
		assertThat(uri, not(hasQueryParam("quux")));
	}
	
	@Test
	public void testHasQueryParamValue() throws Exception {
		assertThat(uri, hasQueryParam("bar", is("baz")));
		assertThat(uri, not(hasQueryParam("bar", is("xxx"))));
		assertThat(uri, hasQueryParam("qux", is("quux")));
		assertThat(uri, hasQueryParam("qux", startsWith("quu")));
	}
	
	@Test
	public void testHasFragmentParam() throws Exception {
		assertThat(uri, hasFragmentParam("courge"));
		assertThat(uri, not(hasFragmentParam("xxx")));
		assertThat(uri, hasFragmentParam("garply"));
		assertThat(uri, not(hasFragmentParam("yyy")));
	}
	
	@Test
	public void testHasFragmentParamValue() throws Exception {
		assertThat(uri, hasFragmentParam("courge", is("grault")));
		assertThat(uri, not(hasFragmentParam("courge", is("xxx"))));
		assertThat(uri, hasFragmentParam("garply", is("waldo")));
		assertThat(uri, hasFragmentParam("garply", startsWith("wa")));
	}
}
