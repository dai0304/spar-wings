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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.springframework.util.Assert;

/**
 * TODO for daisuke
 * 
 * @param <T>
 * @since #version#
 * @author daisuke
 */
@ToString
public class ChunkedResources<T> {
	
	private final Map<String, Object> content;
	
	private ChunkMetadata metadata;
	
	
	/**
	 * Creates a {@link ChunkedResources} instance with {@link Chunk}.
	 * 
	 * @param key must not be {@code null}.
	 * @param chunk The {@link Chunk}
	 * @since #version#
	 */
	public ChunkedResources(String key, Chunk<T> chunk) {
		this(key, chunk.getContent(), new ChunkMetadata(chunk.getContent().size(), chunk.getLastEvaluatedKey()));
	}
	
	/**
	 * Creates a {@link ChunkedResources} instance with content collection.
	 * 
	 * @param key must not be {@code null}.
	 * @param content The contents
	 * @since #version#
	 */
	public ChunkedResources(String key, Collection<T> content) {
		this(key, content, new ChunkMetadata(content.size(), null));
	}
	
	/**
	 * Creates a {@link ChunkedResources} instance with iterable and metadata.
	 * 
	 * @param key must not be {@code null}.
	 * @param iterable must not be {@code null}.
	 * @param metadata must not be {@code null}.
	 * @since #version#
	 */
	public ChunkedResources(String key, Iterable<T> iterable, ChunkMetadata metadata) {
		Assert.notNull(key);
		Assert.notNull(iterable);
		Assert.notNull(metadata);
		this.content = new LinkedHashMap<>();
		if (Iterables.isEmpty(iterable) == false) {
			this.content.put(key, Lists.newArrayList(iterable));
		}
		this.metadata = metadata;
	}
	
	/**
	 * Returns the underlying elements.
	 * 
	 * @return the content will never be {@literal null}.
	 * @since #version#
	 */
	@XmlElement(name = "embedded")
	@com.fasterxml.jackson.annotation.JsonProperty("_embedded")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	public Map<String, Object> getContent() {
		if (content == null || content.isEmpty()) {
			return null;
		} else {
			return Collections.unmodifiableMap(content);
		}
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @return
	 * @since #version#
	 */
	@XmlElement(name = "chunk")
	@com.fasterxml.jackson.annotation.JsonProperty("chunk")
	public ChunkMetadata getMetadata() {
		return metadata;
	}
	
	
	/**
	 * Value object for pagination metadata.
	 * 
	 * @since #version#
	 */
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class ChunkMetadata {
		
		@XmlAttribute
		@com.fasterxml.jackson.annotation.JsonProperty("size")
		@Getter
		private long size;
		
		@XmlAttribute
		@com.fasterxml.jackson.annotation.JsonProperty("last_evaluated_key")
		@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
		@Getter
		private Object lastEvaluatedKey;
	}
}
