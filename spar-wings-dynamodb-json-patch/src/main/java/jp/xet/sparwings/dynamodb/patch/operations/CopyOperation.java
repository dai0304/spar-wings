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

/**
 * TODO for daisuke
 * 
 * @since 0.13
 * @version $Id$
 * @author daisuke
 */
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.PathSetAction;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * JSON Patch {@code copy} operation
 *
 * <p>For this operation, {@code from} is the JSON Pointer of the value to copy,
 * and {@code path} is the destination where the value should be copied.</p>
 *
 * <p>As for {@code add}:</p>
 *
 * <ul>
 *     <li>the value at the destination path is either created or replaced;</li>
 *     <li>it is created only if the immediate parent exists;</li>
 *     <li>{@code -} appends at the end of an array.</li>
 * </ul>
 *
 * <p>It is an error if {@code from} fails to resolve to a JSON value.</p>
 */
public class CopyOperation extends DualPathOperation {
	
	@JsonCreator
	public CopyOperation(@JsonProperty("from") JsonPointer from, @JsonProperty("path") JsonPointer path) {
		super("copy", from, path);
	}

	public CopyOperation(com.github.fge.jsonpatch.CopyOperation o) {
		super(o);
	}
	
	@Override
	public void applyToBuilder(ExpressionSpecBuilder builder) {
		String copyPath = pathGenerator.apply(from);
		String setPath = pathGenerator.apply(path);
		//set the attribute in the path location
		builder.addUpdate(new PathSetAction(ExpressionSpecBuilder.attribute(setPath),
				ExpressionSpecBuilder.attribute(copyPath)));
	}
}
