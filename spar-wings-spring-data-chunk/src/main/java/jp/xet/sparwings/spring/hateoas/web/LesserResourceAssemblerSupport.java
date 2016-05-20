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
package jp.xet.sparwings.spring.hateoas.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.util.Assert;

/**
 * Base class to implement {@link ResourceAssembler}s.
 * Will NOT automate {@link ResourceSupport} instance creation and make sure a self-link is always added.
 * 
 * @param <T> source type
 * @param <D> destination resource type
 * @since #vesrion#
 * @author daisuke
 */
public abstract class LesserResourceAssemblerSupport<T, D extends ResourceSupport> implements ResourceAssembler<T, D> {
	
	private final Class<?> controllerClass;
	
	
	/**
	 * Creates a new {@link ResourceAssemblerSupport} using the given controller class and resource type.
	 * 
	 * @param controllerClass must not be {@code null}.
	 * @since #vesrion#
	 */
	public LesserResourceAssemblerSupport(Class<?> controllerClass) {
		Assert.notNull(controllerClass);
		this.controllerClass = controllerClass;
	}
	
	/**
	 * Converts all given entities into resources.
	 * 
	 * @param entities must not be {@code null}.
	 * @return resources
	 * @see #toResource(Object)
	 * @since #vesrion#
	 */
	public List<D> toResources(Iterable<? extends T> entities) {
		Assert.notNull(entities);
		return StreamSupport.stream(entities.spliterator(), false)
			.map(entity -> toResource(entity))
			.collect(Collectors.toList());
	}
	
	/**
	 * Creates a new resource with a self link to the given id.
	 * 
	 * @param id must not be {@literal null}.
	 * @param entity must not be {@literal null}.
	 * @return resource
	 * @since #vesrion#
	 */
	protected D createResourceWithId(Object id, T entity) {
		return createResourceWithId(id, entity, new Object[0]);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param id must not be {@literal null}.
	 * @param entity must not be {@literal null}.
	 * @param parameters additional parameters to bind to the URI template declared in the annotation, must not be {@code null}.
	 * @return recource
	 * @since #vesrion#
	 */
	protected D createResourceWithId(Object id, T entity, Object... parameters) {
		Assert.notNull(entity);
		Assert.notNull(id);
		
		D instance = toResource(entity);
		instance.add(linkTo(controllerClass, parameters).slash(id).withSelfRel());
		return instance;
	}
}
