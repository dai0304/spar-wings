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
package jp.xet.sparwings.spring.data.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.util.Assert;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO for daisuke
 * 
 * @param <T>
 * @since 0.11
 * @author daisuke
 */
@ToString
@XmlRootElement(name = "chunkedEntities")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ChunkedResources<T> {
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	@XmlElement(name = "embedded")
	@com.fasterxml.jackson.annotation.JsonProperty("_embedded")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private Map<String, Collection<T>> content;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	@XmlElement(name = "chunk")
	@com.fasterxml.jackson.annotation.JsonProperty("chunk")
	private ChunkMetadata metadata;
	
	/**
	 * Creates a {@link ChunkedResources} instance with {@link Chunk}.
	 * 
	 * @param key must not be {@code null}.
	 * @param chunk The {@link Chunk}
	 * @param wrapperFunction function coverts {@code U} to {@code T}
	 * @since 0.28
	 */
	public <U> ChunkedResources(String key, Chunk<U> chunk, Function<U, T> wrapperFunction) {
		this(key, StreamSupport.stream(chunk.spliterator(), false)
			.map(wrapperFunction)
			.collect(Collectors.toList()), new ChunkMetadata(chunk));
	}
	
	/**
	 * Creates a {@link ChunkedResources} instance with {@link Chunk}.
	 * 
	 * @param key must not be {@code null}.
	 * @param chunk The {@link Chunk}
	 * @since 0.11
	 */
	public ChunkedResources(String key, Chunk<T> chunk) {
		this(key, chunk.getContent(), new ChunkMetadata(chunk));
	}
	
	/**
	 * Creates a {@link ChunkedResources} instance with content collection.
	 * 
	 * @param key must not be {@code null}.
	 * @param content The contents
	 * @since 0.11
	 */
	public ChunkedResources(String key, Collection<T> content) {
		this(key, content, new ChunkMetadata(content.size(), null));
	}
	
	/**
	 * Creates a {@link ChunkedResources} instance with iterable and metadata.
	 * 
	 * @param key must not be {@code null}.
	 * @param content must not be {@code null}.
	 * @param metadata must not be {@code null}.
	 * @since 0.11
	 */
	public ChunkedResources(String key, Collection<T> content, ChunkMetadata metadata) {
		Assert.notNull(key);
		Assert.notNull(content);
		Assert.notNull(metadata);
		this.content = Collections.singletonMap(key, content);
		this.metadata = metadata;
	}
	
	/**
	 * Value object for pagination metadata.
	 * 
	 * @since 0.11
	 */
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	public static class ChunkMetadata {
		
		@XmlAttribute
		@com.fasterxml.jackson.annotation.JsonProperty("size")
		@Getter(onMethod = @__(@JsonIgnore))
		private long size;
		
		@XmlAttribute
		@com.fasterxml.jackson.annotation.JsonProperty("pagination_token")
		@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
		@Getter(onMethod = @__(@JsonIgnore))
		private String paginationToken;
		
		/**
		 * インスタンスを生成する。
		 * 
		 * @param chunk
		 */
		public ChunkMetadata(Chunk<?> chunk) {
			this(chunk.getContent().size(), chunk.getPaginationToken());
		}
	}
}
