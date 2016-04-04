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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * TODO for daisuke
 * 
 * @since 0.15
 * @version $Id$
 * @author daisuke
 */
@RequiredArgsConstructor
public class HasQueryParam extends TypeSafeMatcher<URI> {
	
	private final String name;
	
	private final Matcher<String> matcher;
	
	
	@Override
	public void describeTo(Description mismatchDescription) {
		if (matcher == null) {
			mismatchDescription.appendText("query has ").appendValue(name).appendText(" parameter");
		} else {
			mismatchDescription.appendText("query parameter ").appendValue(name)
				.appendText(" is ").appendDescriptionOf(matcher);
		}
	}
	
	@Override
	protected boolean matchesSafely(URI item) {
		List<NameValuePair> parsedParams = URLEncodedUtils.parse(item, "UTF-8");
		List<NameValuePair> nvps = parsedParams.stream()
			.filter(nvp -> nvp.getName().equals(name))
			.collect(Collectors.toList());
		
		if (nvps.isEmpty()) {
			return false;
		}
		if (matcher == null) {
			return true;
		}
		return nvps.stream().anyMatch(nvp -> matcher.matches(nvp.getValue()));
	}
}
