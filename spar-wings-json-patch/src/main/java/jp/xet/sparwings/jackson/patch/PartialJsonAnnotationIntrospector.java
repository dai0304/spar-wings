/*
 * Copyright 2015-2016 Classmethod, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All source code, documentation and other information
 * contained herein is, and remains the property of Classmethod, Inc.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Classmethod, Inc.
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
