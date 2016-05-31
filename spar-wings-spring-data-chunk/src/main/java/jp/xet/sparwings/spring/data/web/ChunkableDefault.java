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
package jp.xet.sparwings.spring.data.web;

import org.springframework.data.domain.Sort.Direction;

/**
 * Annotation to set defaults when injecting a {@link jp.xet.sparwings.spring.data.chunk.Chunkable} into a controller
 * method.
 * 
 * @since 0.19
 * @author daisuke
 */
public @interface ChunkableDefault {
	
	String DEFAULT = "**default**";
	
	
	/**
	 * Alias for {@link #size()}. Prefer to use the {@link #size()} method as it makes the annotation declaration more
	 * expressive and you'll probably want to configure the {@link #page()} anyway.
	 * 
	 * @return
	 */
	int value() default 10;
	
	/**
	 * The default-size the injected {@link jp.xet.sparwings.spring.data.chunk.Chunkable} should get if no corresponding
	 * parameter defined in request (default is 10).
	 */
	int size() default 10;
	
	/**
	 * The default-pagenumber the injected {@link jp.xet.sparwings.spring.data.chunk.Chunkable} should get if no corresponding
	 * parameter defined in request (default is "").
	 */
	String after() default DEFAULT;
	
	String before() default DEFAULT;
	
	/**
	 * The direction to sort by. Defaults to {@link Direction#ASC}.
	 * 
	 * @return
	 */
	Direction direction() default Direction.ASC;
	
}
