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

import java.util.List;

import jp.xet.sparwings.spring.data.chunk.Chunkable;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MethodLinkBuilderFactory;
import org.springframework.hateoas.core.MethodParameters;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * TODO for daisuke
 * 
 * @since 0.20
 * @author daisuke
 */
@Slf4j
public class ChunkedResourcesAssemblerArgumentResolver implements HandlerMethodArgumentResolver {
	
	private static final String SUPERFLOUS_QUALIFIER =
			"Found qualified {} parameter, but a unique unqualified {} parameter. Using that one, but you might wanna check your controller method configuration!";
	
	private static final String PARAMETER_AMBIGUITY =
			"Discovered muliple parameters of type Chunkable but no qualifier annotations to disambiguate!";
	
	private final HateoasChunkableHandlerMethodArgumentResolver resolver;
	
	private final MethodLinkBuilderFactory<?> linkBuilderFactory;
	
	
	/**
	 * Creates a new {@link ChunkedResourcesAssemblerArgumentResolver} using the given
	 * {@link ChunkableHandlerMethodArgumentResolver} and {@link MethodLinkBuilderFactory}.
	 * 
	 * @param resolver can be {@literal null}.
	 * @param linkBuilderFactory can be {@literal null}, will be defaulted to a {@link ControllerLinkBuilderFactory}.
	 */
	public ChunkedResourcesAssemblerArgumentResolver(HateoasChunkableHandlerMethodArgumentResolver resolver,
			MethodLinkBuilderFactory<?> linkBuilderFactory) {
		this.resolver = resolver;
		this.linkBuilderFactory = linkBuilderFactory == null ? new ControllerLinkBuilderFactory() : linkBuilderFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return ChunkedResourcesAssembler.class.equals(parameter.getParameterType());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		
		UriComponents fromUriString = resolveBaseUri(parameter);
		MethodParameter chunkableParameter = findMatchingChunkableParameter(parameter);
		
		if (chunkableParameter != null) {
			return new MethodParameterAwareChunkedResourcesAssembler<>(resolver, chunkableParameter, fromUriString);
		} else {
			return new ChunkedResourcesAssembler<>(resolver, fromUriString);
		}
	}
	
	/**
	 * Eagerly resolve a base URI for the given {@link MethodParameter} to be handed to the assembler.
	 * 
	 * @param parameter must not be {@literal null}.
	 * @return the {@link UriComponents} representing the base URI, or {@literal null} if it can't be resolved eagerly.
	 */
	private UriComponents resolveBaseUri(MethodParameter parameter) {
		
		try {
			Link linkToMethod =
					linkBuilderFactory.linkTo(parameter.getDeclaringClass(), parameter.getMethod()).withSelfRel();
			return UriComponentsBuilder.fromUriString(linkToMethod.getHref()).build();
		} catch (IllegalArgumentException o_O) {
			return null;
		}
	}
	
	/**
	 * Returns finds the {@link MethodParameter} for a {@link Chunkable} instance matching the given
	 * {@link MethodParameter} requesting a {@link ChunkedResourcesAssembler}.
	 * 
	 * @param parameter must not be {@literal null}.
	 * @return
	 */
	private static final MethodParameter findMatchingChunkableParameter(MethodParameter parameter) {
		
		MethodParameters parameters = new MethodParameters(parameter.getMethod());
		List<MethodParameter> chunkableParameters = parameters.getParametersOfType(Chunkable.class);
		Qualifier assemblerQualifier = parameter.getParameterAnnotation(Qualifier.class);
		
		if (chunkableParameters.isEmpty()) {
			return null;
		}
		
		if (chunkableParameters.size() == 1) {
			
			MethodParameter chunkableParameter = chunkableParameters.get(0);
			MethodParameter matchingParameter = returnIfQualifiersMatch(chunkableParameter, assemblerQualifier);
			
			if (matchingParameter == null) {
				log.info(SUPERFLOUS_QUALIFIER, ChunkedResourcesAssembler.class.getSimpleName(),
						Chunkable.class.getName());
			}
			
			return chunkableParameter;
		}
		
		if (assemblerQualifier == null) {
			throw new IllegalStateException(PARAMETER_AMBIGUITY);
		}
		
		for (MethodParameter chunkableParameter : chunkableParameters) {
			
			MethodParameter matchingParameter = returnIfQualifiersMatch(chunkableParameter, assemblerQualifier);
			
			if (matchingParameter != null) {
				return matchingParameter;
			}
		}
		
		throw new IllegalStateException(PARAMETER_AMBIGUITY);
	}
	
	private static MethodParameter returnIfQualifiersMatch(MethodParameter chunkableParameter,
			Qualifier assemblerQualifier) {
		
		if (assemblerQualifier == null) {
			return chunkableParameter;
		}
		
		Qualifier chunkableParameterQualifier = chunkableParameter.getParameterAnnotation(Qualifier.class);
		
		if (chunkableParameterQualifier == null) {
			return null;
		}
		
		return chunkableParameterQualifier.value().equals(assemblerQualifier.value()) ? chunkableParameter : null;
	}
}
