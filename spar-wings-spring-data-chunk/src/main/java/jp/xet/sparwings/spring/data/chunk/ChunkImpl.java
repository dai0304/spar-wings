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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

/**
 * TODO for daisuke
 * 
 * @param <T>
 * @since 0.11
 * @author daisuke
 */
@EqualsAndHashCode(of = {
	"content",
	"lastKey",
	"firstKey"
})
public class ChunkImpl<T> implements Chunk<T> {
	
	@JsonProperty
	private final List<T> content = new ArrayList<>();
	
	@JsonProperty
	@Getter
	private final String lastKey;
	
	@JsonProperty
	@Getter
	private final String firstKey;
	
	@JsonIgnore
	@Getter
	private final Chunkable chunkable;
	
	
	/**
	 * Creates a new {@link Chunk} with the given content and the given governing {@link Pageable}.
	 * 
	 * @param content must not be {@literal null}.
	 * @param lastKey
	 * @param firstKey
	 * @param chunkable can be {@literal null}.
	 * @since 0.11
	 */
	public ChunkImpl(List<T> content, String lastKey, String firstKey, Chunkable chunkable) {
		Assert.notNull(content, "Content must not be null!");
		this.content.addAll(content);
		this.lastKey = lastKey;
		this.firstKey = firstKey;
		this.chunkable = chunkable;
	}
	
	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}
	
	@Override
	public List<T> getContent() {
		return Collections.unmodifiableList(content);
	}
	
	@Override
	public Direction getDirection() {
		return chunkable == null ? null : chunkable.getDirection();
	}
	
	@Override
	public boolean hasContent() {
		return content.isEmpty() == false;
	}
	
	@Override
	public boolean hasNext() {
		return lastKey != null && isLast() == false;
	}
	
	@Override
	public boolean hasPrev() {
		return firstKey != null && isFirst() == false;
	}
	
	@Override
	public boolean isLast() {
		return content.size() < chunkable.getMaxPageSize();
	}
	
	@Override
	public boolean isFirst() {
		return chunkable.getAfterKey() == null;
	}
	
	@Override
	public Chunkable nextChunkable() {
		return hasNext() ? chunkable.next(lastKey) : null;
	}
	
	@Override
	public Chunkable prevChunkable() {
		return hasPrev() ? chunkable.prev(firstKey) : null;
	}
	
	@Override
	public <S>Chunk<S> map(Converter<? super T, ? extends S> converter) {
		return new ChunkImpl<>(getConvertedContent(converter), lastKey, firstKey, chunkable);
	}
	
	/**
	 * Applies the given {@link Converter} to the content of the {@link Chunk}.
	 * 
	 * @param converter must not be {@literal null}.
	 * @return
	 * @since 0.11
	 */
	protected <S>List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {
		Assert.notNull(converter, "Converter must not be null!");
		return content.stream().map(converter::convert).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		String contentType = "UNKNOWN";
		List<T> content = getContent();
		
		if (content.size() > 0) {
			contentType = content.get(0).getClass().getName();
		}
		
		return String.format("Chunk containing %s instances", contentType);
	}
}
