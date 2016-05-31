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

import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import jp.xet.sparwings.spring.data.chunk.Chunkable;
import jp.xet.sparwings.spring.hateoas.ChunkedResources;
import jp.xet.sparwings.spring.hateoas.ChunkedResources.ChunkMetadata;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * TODO for daisuke
 * 
 * @since 0.20
 * @author daisuke
 */
public class ChunkedResourcesAssembler<T> implements ResourceAssembler<Chunk<T>, ChunkedResources<Resource<T>>> {
	
//	private final HateoasChunkableHandlerMethodArgumentResolver chunkableResolver;
	
	private final UriComponents baseUri;
	
	private final EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
	
	private HateoasChunkableHandlerMethodArgumentResolver chunkableResolver;
	
	
	/**
	 * Creates a new {@link ChunkedResourcesAssembler} using the given {@link ChunkableHandlerMethodArgumentResolver} and
	 * base URI. If the former is {@literal null}, a default one will be created. If the latter is {@literal null}, calls
	 * to {@link #toResource(Chunk)} will use the current request's URI to build the relevant previous and next links.
	 * 
	 * @param resolver
	 * @param baseUri
	 */
	public ChunkedResourcesAssembler(HateoasChunkableHandlerMethodArgumentResolver resolver, UriComponents baseUri) {
		this.chunkableResolver = resolver == null ? new HateoasChunkableHandlerMethodArgumentResolver() : resolver;
		this.baseUri = baseUri;
	}
	
	@Override
	public ChunkedResources<Resource<T>> toResource(Chunk<T> entity) {
		return toResource("items", entity, new SimpleChunkedResourceAssembler<T>());
	}
	
	/**
	 * Creates a new {@link ChunkedResources} by converting the given {@link Chunk} into a {@link ChunkMetadata} instance and
	 * wrapping the contained elements into {@link Resource} instances. Will add pagination links based on the given the
	 * self link.
	 * 
	 * @param page must not be {@literal null}.
	 * @param selfLink must not be {@literal null}.
	 * @return
	 */
	public ChunkedResources<Resource<T>> toResource(String key, Chunk<T> page, Link selfLink) {
		return toResource(key, page, new SimpleChunkedResourceAssembler<T>(), selfLink);
	}
	
	/**
	 * Creates a new {@link ChunkedResources} by converting the given {@link Chunk} into a {@link ChunkMetadata} instance and
	 * using the given {@link ResourceAssembler} to turn elements of the {@link Chunk} into resources.
	 * 
	 * @param page must not be {@literal null}.
	 * @param assembler must not be {@literal null}.
	 * @return
	 */
	public <R extends ResourceSupport>ChunkedResources<R> toResource(String key, Chunk<T> page,
			ResourceAssembler<T, R> assembler) {
		return createResource(key, page, assembler, null);
	}
	
	/**
	 * Creates a new {@link ChunkedResources} by converting the given {@link Chunk} into a {@link ChunkMetadata} instance and
	 * using the given {@link ResourceAssembler} to turn elements of the {@link Chunk} into resources. Will add pagination
	 * links based on the given the self link.
	 * 
	 * @param page must not be {@literal null}.
	 * @param assembler must not be {@literal null}.
	 * @param link must not be {@literal null}.
	 * @return
	 */
	public <R extends ResourceSupport>ChunkedResources<R> toResource(String key, Chunk<T> page,
			ResourceAssembler<T, R> assembler, Link link) {
		Assert.notNull(link, "Link must not be null!");
		return createResource(key, page, assembler, link);
	}
	
	/**
	 * Creates a {@link ChunkedResources} with an empt collection {@link EmbeddedWrapper} for the given domain type.
	 * 
	 * @param page must not be {@literal null}, content must be empty.
	 * @param type must not be {@literal null}.
	 * @param link can be {@literal null}.
	 * @return
	 * @since 1.11
	 */
	public ChunkedResources<?> toEmptyResource(String key, Chunk<?> page, Class<?> type, Link link) {
		Assert.notNull(page, "Chunk must must not be null!");
		Assert.isTrue(!page.hasContent(), "Chunk must not have any content!");
		Assert.notNull(type, "Type must not be null!");
		
		ChunkMetadata metadata = asChunkMetadata(page);
		
		EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(type);
		List<EmbeddedWrapper> embedded = Collections.singletonList(wrapper);
		
		return addPaginationLinks(new ChunkedResources<>(key, embedded, metadata), page, link);
	}
	
	private <S, R extends ResourceSupport>ChunkedResources<R> createResource(String key, Chunk<S> page,
			ResourceAssembler<S, R> assembler, Link link) {
		
		Assert.notNull(page, "Chunk must not be null!");
		Assert.notNull(assembler, "ResourceAssembler must not be null!");
		
		List<R> resources = new ArrayList<>(page.getContent().size());
		
		for (S element : page) {
			resources.add(assembler.toResource(element));
		}
		
		return addPaginationLinks(new ChunkedResources<>(key, resources, asChunkMetadata(page)), page, link);
	}
	
	private <R>ChunkedResources<R> addPaginationLinks(ChunkedResources<R> resources, Chunk<?> chunk, Link link) {
		UriTemplate base = getUriTemplate(link);
		
		resources.add(createLink(base, chunk.getChunkable(), Link.REL_SELF));
		if (chunk.hasNext()) {
			resources.add(createLink(base, chunk.nextChunkable(), Link.REL_NEXT));
		}
		if (chunk.hasPrev()) {
			resources.add(createLink(base, chunk.prevChunkable(), Link.REL_PREVIOUS));
		}
		
		return resources;
	}
	
	/**
	 * Returns a default URI string either from the one configured on assembler creatino or by looking it up from the
	 * current request.
	 * 
	 * @return
	 */
	private UriTemplate getUriTemplate(Link baseLink) {
		String href = baseLink != null
				? baseLink.getHref()
				: baseUri == null
						? ServletUriComponentsBuilder.fromCurrentRequest().build().toString()
						: baseUri.toString();
		
		return new UriTemplate(href);
	}
	
	/**
	 * Creates a {@link Link} with the given rel that will be based on the given {@link UriTemplate} but enriched with the
	 * values of the given {@link Chunkable} (if not {@literal null}).
	 * 
	 * @param base must not be {@literal null}.
	 * @param chunkable can be {@literal null}
	 * @param rel must not be {@literal null} or empty.
	 * @return
	 */
	private Link createLink(UriTemplate base, Chunkable chunkable, String rel) {
		UriComponentsBuilder builder = fromUri(base.expand());
		chunkableResolver.enhance(builder, getMethodParameter(), chunkable);
		return new Link(new UriTemplate(builder.build().toString()), rel);
	}
	
	/**
	 * Return the {@link MethodParameter} to be used to potentially qualify the paging and sorting request parameters to.
	 * Default implementations returns {@literal null}, which means the parameters will not be qualified.
	 * 
	 * @return
	 * @since 1.7
	 */
	protected MethodParameter getMethodParameter() {
		return null;
	}
	
	/**
	 * Creates a new {@link ChunkMetadata} instance from the given {@link Chunk}.
	 * 
	 * @param chunk must not be {@literal null}.
	 * @return
	 */
	private static <T>ChunkMetadata asChunkMetadata(Chunk<T> chunk) {
		Assert.notNull(chunk, "Chunk must not be null!");
		return new ChunkMetadata(chunk.getContent().size(), chunk.getLastKey(), chunk.getFirstKey());
	}
	
	
	private static class SimpleChunkedResourceAssembler<T> implements ResourceAssembler<T, Resource<T>> {
		
		@Override
		public Resource<T> toResource(T entity) {
			return new Resource<>(entity);
		}
	}
}
