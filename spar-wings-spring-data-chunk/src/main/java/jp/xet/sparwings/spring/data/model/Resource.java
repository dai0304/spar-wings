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

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * TODO for daisuke
 */
@JsonIgnoreProperties("content")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class Resource<T> {
	
	@Getter
	@JsonUnwrapped
	private T value;
	
	private final Map<String, Link> links = new HashMap<>();
	
	private final Map<String, Object> embeddedResources = new HashMap<>();
	
	
	/**
	 * Adds the given link to the resource.
	 * 
	 * @param rel
	 * @param link
	 */
	public void add(String rel, Link link) {
		Assert.notNull(link, "Link must not be null!");
		this.links.put(rel, link);
	}
	
	/**
	 * Returns whether the resource contains {@link Link}s at all.
	 * 
	 * @return
	 */
	public boolean hasLinks() {
		return links.isEmpty() == false;
	}
	
	/**
	 * Returns whether the resource contains a {@link Link} with the given rel.
	 * 
	 * @param rel
	 * @return
	 */
	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}
	
	/**
	 * Returns all {@link Link}s contained in this resource.
	 * 
	 * @return
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty("_links")
	public Map<String, Link> getLinks() {
		return links;
	}
	
	/**
	 * Removes all {@link Link}s added to the resource so far.
	 */
	public void clearLinks() {
		this.links.clear();
	}
	
	/**
	 * Returns the link with the given rel.
	 * 
	 * @param rel
	 * @return the link with the given rel or {@literal null} if none found.
	 */
	public Link getLink(String rel) {
		return links.get(rel);
	}
	
	/**
	 * Returns HAL embedded resource map.
	 * 
	 * @return HAL embedded resource map.
	 * @since 0.22
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return embeddedResources;
	}
	
	/**
	 * Add HAL embedded resource.
	 * 
	 * @param relationship 関係
	 * @param resource 埋め込みリソース
	 * @since 0.22
	 */
	public void embedResource(String relationship, Object resource) {
		embeddedResources.put(relationship, resource);
	}
}
