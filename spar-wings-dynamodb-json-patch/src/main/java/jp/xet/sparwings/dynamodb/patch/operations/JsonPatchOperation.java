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
package jp.xet.sparwings.dynamodb.patch.operations;

import java.lang.reflect.Field;
import java.util.function.Function;

import jp.xet.sparwings.dynamodb.patch.JsonPathToAttributePath;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.github.fge.jackson.jsonpointer.JsonPointer;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "op")
@JsonSubTypes({
	@Type(name = "add", value = AddOperation.class),
	@Type(name = "copy", value = CopyOperation.class),
	@Type(name = "move", value = MoveOperation.class),
	@Type(name = "remove", value = RemoveOperation.class),
	@Type(name = "replace", value = ReplaceOperation.class),
	@Type(name = "test", value = TestOperation.class)
})
/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
@RequiredArgsConstructor
public abstract class JsonPatchOperation implements JsonSerializable {
	
	@NonNull
	@Setter
	Function<JsonPointer, String> pathGenerator = new JsonPathToAttributePath();
	
	@Getter
	final String op;
	
	/*
	 * Note: no need for a custom deserializer, Jackson will try and find a
	 * constructor with a single string argument and use it.
	 *
	 * However, we need to serialize using .toString().
	 */
	@Getter
	final JsonPointer path;
	
	
	public abstract void applyToBuilder(ExpressionSpecBuilder builder);

	/**
	 * creates a sparwings json patch operation from a github json patch operation
	 * @param jpo the path value operation to convert
	 */
	protected JsonPatchOperation(com.github.fge.jsonpatch.JsonPatchOperation jpo) {
		path = getProtected(com.github.fge.jsonpatch.JsonPatchOperation.class, "path", jpo);
		op = getProtected(com.github.fge.jsonpatch.JsonPatchOperation.class, "op", jpo);
	}

	public static <T, E> T getProtected(Class<E> clazz, String fieldName, E object) {
		final Field field;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(String.format("unable to field %s field", fieldName), e);
		}
		try {
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(String.format("unable to get %s field from object", fieldName), e);
		}
	}
}
