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

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
class JsonPatches<T> implements UpdateRequest<T> {
	
	private final ObjectMapper mapper;
	
	private final JsonNode node;
	
	
	@Override
	@SuppressWarnings("unchecked")
	public T apply(T original) throws IllegalPatchException {
		try {
			JsonPatch patch = JsonPatch.fromJson(this.node);
			JsonNode patched = patch.apply(mapper.valueToTree(original));
			return mapper.treeToValue(patched, (Class<T>) original.getClass());
		} catch (IllegalArgumentException | JsonPatchException | IOException e) {
			throw new IllegalPatchException(e);
		}
	}
}
