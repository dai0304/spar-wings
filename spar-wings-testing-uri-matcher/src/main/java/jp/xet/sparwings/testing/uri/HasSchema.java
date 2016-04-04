/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/04/02
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.xet.sparwings.testing.uri;

import java.net.URI;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import lombok.RequiredArgsConstructor;


/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@RequiredArgsConstructor
public class HasSchema extends TypeSafeMatcher<URI> {
	
	private final Matcher<?> matcher;

	@Override
	public void describeTo(Description mismatchDescription) {
		mismatchDescription.appendText("scheme is ").appendDescriptionOf(matcher);
	}

	@Override
	protected boolean matchesSafely(URI item) {
		return matcher.matches(item.getScheme());
	}
}
