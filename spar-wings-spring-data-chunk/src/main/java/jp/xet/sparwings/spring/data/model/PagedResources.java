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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * TODO for daisuke
 * 
 * @param <T>
 * @since 0.11
 * @author daisuke
 */
@ToString
@XmlRootElement(name = "pagedEntities")
public class PagedResources<T> {
	
	private final Map<String, Object> content;
	
	private PageMetadata metadata;
	
	
	/**
	 * Creates a {@link PagedResources} instance with {@link Chunk}.
	 * 
	 * @param key must not be {@code null}.
	 * @param page The {@link Chunk}
	 * @since 0.11
	 */
	public PagedResources(String key, Page<T> page) {
		this(key, page.getContent(), new PageMetadata(page));
	}
	
	/**
	 * Creates a {@link PagedResources} instance with content collection.
	 * 
	 * @param key must not be {@code null}.
	 * @param content The contents
	 * @since 0.11
	 */
	public PagedResources(String key, Collection<T> content) {
		this(key, content, new PageMetadata(content.size(), null, null, null));
	}
	
	/**
	 * Creates a {@link PagedResources} instance with iterable and metadata.
	 * 
	 * @param key must not be {@code null}.
	 * @param content must not be {@code null}.
	 * @param metadata must not be {@code null}.
	 * @since 0.11
	 */
	public PagedResources(String key, Collection<T> content, PageMetadata metadata) {
		Assert.notNull(key);
		Assert.notNull(content);
		Assert.notNull(metadata);
		this.content = new LinkedHashMap<>();
		if (content.isEmpty() == false) {
			this.content.put(key, content);
		}
		this.metadata = metadata;
	}
	
	/**
	 * Returns the underlying elements.
	 * 
	 * @return the content will never be {@literal null}.
	 * @since 0.11
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
	 * @since 0.11
	 */
	@XmlElement(name = "page")
	@com.fasterxml.jackson.annotation.JsonProperty("page")
	public PageMetadata getMetadata() {
		return metadata;
	}
	
	
	/**
	 * Value object for pagination metadata.
	 * 
	 * @since 0.11
	 */
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class PageMetadata {
		
		/** the requested size of the page */
		@XmlAttribute
		@JsonProperty("size")
		@Getter(onMethod = @__({
			@JsonIgnore
		}))
		private long size;
		
		/** the total number of elements available */
		@XmlAttribute
		@JsonProperty("total_elements")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@Getter(onMethod = @__({
			@JsonIgnore
		}))
		private Long totalElements;
		
		/** how many pages are available in total */
		@XmlAttribute
		@JsonProperty("total_pages")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@Getter(onMethod = @__({
			@JsonIgnore
		}))
		private Long totalPages;
		
		/** the number of the current page */
		@XmlAttribute
		@JsonProperty("number")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@Getter(onMethod = @__({
			@JsonIgnore
		}))
		private Long number;
		
		
		/**
		 * Creates a new {@link PageMetadata} from the given size, numer and total elements.
		 * 
		 * @param size the size of the page
		 * @param number the number of the page
		 * @param totalElements the total number of elements available
		 */
		public PageMetadata(long size, long number, long totalElements) {
			this(size, number, totalElements, size == 0 ? 0 : (long) Math.ceil((double) totalElements / (double) size));
		}
		
		public PageMetadata(Page<?> page) {
			this(
					Integer.valueOf(page.getSize()).longValue(),
					page.getTotalElements(),
					Integer.valueOf(page.getTotalPages()).longValue(),
					Integer.valueOf(page.getNumber()).longValue());
		}
	}
}
