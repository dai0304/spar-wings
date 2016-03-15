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
package jp.xet.sparwings.spring.data.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

/**
 * TODO for daisuke
 */
public class ChunkImpl<T> implements Chunk<T> {
	
	@JsonProperty
	private final List<T> content = new ArrayList<>();
	
	@JsonProperty
	@Getter
	private final Object lastEvaluatedKey;
	
	@JsonIgnore
	private final Chunkable chunkable;
	
	
	/**
	 * Creates a new {@link Chunk} with the given content and the given governing {@link Pageable}.
	 * 
	 * @param content must not be {@literal null}.
	 * @param lastEvaluatedKey
	 * @param chunkable can be {@literal null}.
	 */
	public ChunkImpl(List<T> content, Object lastEvaluatedKey, Chunkable chunkable) {
		Assert.notNull(content, "Content must not be null!");
		this.content.addAll(content);
		this.lastEvaluatedKey = lastEvaluatedKey;
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
		return lastEvaluatedKey != null;
	}
	
	@Override
	public boolean isLast() {
		return hasNext() == false;
	}
	
	@Override
	public Chunkable nextChunkable() {
		return hasNext() ? chunkable.next(lastEvaluatedKey) : null;
	}
	
	@Override
	public <S>Chunk<S> map(Converter<? super T, ? extends S> converter) {
		return new ChunkImpl<>(getConvertedContent(converter), lastEvaluatedKey, chunkable);
	}
	
	/**
	 * Applies the given {@link Converter} to the content of the {@link Chunk}.
	 * 
	 * @param converter must not be {@literal null}.
	 * @return
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((lastEvaluatedKey == null) ? 0 : lastEvaluatedKey.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChunkImpl<?> other = (ChunkImpl<?>) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (lastEvaluatedKey == null) {
			if (other.lastEvaluatedKey != null) {
				return false;
			}
		} else if (!lastEvaluatedKey.equals(other.lastEvaluatedKey)) {
			return false;
		}
		return true;
	}
	
}
