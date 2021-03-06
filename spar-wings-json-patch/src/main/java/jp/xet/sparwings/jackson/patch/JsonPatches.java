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

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@ToString
@RequiredArgsConstructor
class JsonPatches<T> implements UpdateRequest<T> {
	
	private final ObjectMapper mapper;
	
	/** JSON Patch document node */
	private final JsonNode node;
	
	
	@Override
	@SuppressWarnings("unchecked")
	public T apply(T original) throws IllegalPatchException {
		String json = null;
		try {
			JsonPatch patch = JsonPatch.fromJson(node);
			json = mapper.writeValueAsString(original);
			JsonNode originalNode = mapper.readTree(json);
			JsonNode patchedNode = patch.apply(originalNode);
			T patched = mapper.treeToValue(patchedNode, (Class<T>) original.getClass());
			return patched; // NOPMD
		} catch (JsonParseException e) {
			throw new IllegalStateException("Failed to parse original JSON:" + json, e);
		} catch (IllegalArgumentException | JsonPatchException | IOException | NullPointerException e) { // NOPMD
			throw new IllegalPatchException(e);
		}
	}
}
