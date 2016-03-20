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
package jp.xet.sparwings.jackson.patch;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;

import lombok.RequiredArgsConstructor;

/**
 * TODO for daisuke
 */
@SuppressWarnings("serial")
@RequiredArgsConstructor
public class PartialJsonAnnotationIntrospector extends AnnotationIntrospector {
	
	private final ObjectMapper mapper;
	
	
	@Override
	public Object findDeserializer(Annotated a) {
		if (UpdateRequest.class.isAssignableFrom(a.getRawType())) {
			return new UpdateRequestDeserializer(mapper);
		}
		return null;
	}
	
	@Override
	public Object findSerializer(Annotated a) {
		if (Difference.class.isAssignableFrom(a.getRawType())) {
			return new DifferenceSerializer(mapper);
		}
		return null;
	}
	
	@Override
	public Version version() {
		return Version.unknownVersion();
	}
}
