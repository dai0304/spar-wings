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
package jp.xet.sparwings.spring.data.chunk;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort.Direction;

/**
 * A part of item set.
 * 
 * @param <T> Type of item
 * @since 0.11
 * @author daisuke
 */
public interface Chunk<T>extends Iterable<T> {
	
	/**
	 * Returns the chunk content as {@link List}.
	 * 
	 * @return chunk content list
	 * @since 0.11
	 */
	List<T> getContent();
	
	/**
	 * Returns stream of content.
	 * 
	 * @return stream
	 * @since #version#
	 */
	Stream<T> stream();
	
	/**
	 * Returns pagination token.
	 * 
	 * @return token
	 * @since #version#
	 */
	String getPaginationToken();
	
	/**
	 * Returns the sorting direction for the {@link Chunk}.
	 * 
	 * @return direction
	 * @since 0.11
	 */
	Direction getDirection();
	
	/**
	 * Returns whether the {@link Chunk} has content at all.
	 * 
	 * @return {@code true} if this chunk contains content
	 * @since 0.11
	 */
	boolean hasContent();
	
	/**
	 * Returns if there is a next {@link Chunk}.
	 * 
	 * @return if there is a next {@link Chunk}.
	 * @since 0.11
	 */
	boolean hasNext();
	
	/**
	 * Returns if there is a previous {@link Chunk}.
	 * 
	 * @return if there is a previous {@link Chunk}.
	 * @since 0.11
	 */
	boolean hasPrev();
	
	/**
	 * Returns whether the current {@link Chunk} is the last one.
	 * 
	 * @return {@code true} if this chunk is the last one
	 * @since 0.11
	 */
	boolean isLast();
	
	/**
	 * Returns whether the current {@link Chunk} is the first one.
	 * 
	 * @return {@code true} if this chunk is the first one
	 * @since 0.11
	 */
	boolean isFirst();
	
	/**
	 * Returns the {@link Chunkable} to request the next {@link Chunk}. Can be {@literal null} in case the current
	 * {@link Chunk} is already the last one. Clients should check {@link #hasNext()} before calling this method to make
	 * sure they receive a non-{@literal null} value.
	 * 
	 * @return {@link Chunkable}
	 * @since 0.11
	 */
	Chunkable nextChunkable();
	
	/**
	 * Returns the {@link Chunkable} to request the previous {@link Chunk}. Can be {@literal null} in case the current
	 * {@link Chunk} is already the first one. Clients should check {@link #hasNext()} before calling this method to make
	 * sure they receive a non-{@literal null} value.
	 * 
	 * @return {@link Chunkable}
	 * @since 0.11
	 */
	Chunkable prevChunkable();
	
	/**
	 * Returns a new {@link Chunk} with the content of the current one mapped by the given {@link Converter}.
	 * 
	 * @param converter must not be {@literal null}.
	 * @return a new {@link Chunk} with the content of the current one mapped by the given {@link Converter}.
	 * @since 0.11
	 */
	<S> Chunk<S> map(Converter<? super T, ? extends S> converter);
	
	/**
	 * Returns the {@link Chunkable} used to retrieve current{@link Chunk}.
	 * 
	 * @return {@link Chunkable}
	 * @since 0.21
	 */
	Chunkable getChunkable();
}
