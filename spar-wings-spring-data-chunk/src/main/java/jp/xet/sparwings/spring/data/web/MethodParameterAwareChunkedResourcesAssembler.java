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

import org.springframework.core.MethodParameter;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author daisuke
 */
class MethodParameterAwareChunkedResourcesAssembler<T> extends ChunkedResourcesAssembler<T> {
	
	private final MethodParameter parameter;
	
	
	/**
	 * Creates a new {@link MethodParameterAwareChunkedResourcesAssembler} using the given {@link MethodParameter},
	 * {@link HateoasPageableHandlerMethodArgumentResolver} and base URI.
	 * 
	 * @param parameter must not be {@literal null}.
	 * @param baseUri can be {@literal null}.
	 */
	public MethodParameterAwareChunkedResourcesAssembler(HateoasChunkableHandlerMethodArgumentResolver resolver,
			MethodParameter parameter, UriComponents baseUri) {
		super(resolver, baseUri);
		Assert.notNull(parameter, "Method parameter must not be null!");
		this.parameter = parameter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.web.PagedResourcesAssembler#getMethodParameter()
	 */
	@Override
	protected MethodParameter getMethodParameter() {
		return parameter;
	}
}
