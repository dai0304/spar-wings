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

import java.lang.reflect.Method;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jp.xet.sparwings.spring.data.chunk.ChunkRequest;
import jp.xet.sparwings.spring.data.chunk.Chunkable;
import jp.xet.sparwings.spring.data.chunk.Chunkable.PaginationRelation;

/**
 * Extracts paging information from web requests and thus allows injecting {@link Chunkable} instances into controller
 * methods. Request properties to be parsed can be configured. Default configuration uses request parameters beginning
 * with {@link #DEFAULT_NEXT_PARAMETER}, {@link #DEFAULT_PREV_PARAMETER}, {@link #DEFAULT_SIZE_PARAMETER},
 * {@link #DEFAULT_DIRECTION_PARAMETER}, {@link #DEFAULT_QUALIFIER_DELIMITER}.
 * 
 * @since 0.19
 * @author daisuke
 */
@Slf4j
public class ChunkableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver { // NOPMD - cc
	
	private static final String INVALID_DEFAULT_PAGE_SIZE =
			"Invalid default page size configured for method %s! Must not be less than one!";
	
	private static final String DEFAULT_NEXT_PARAMETER = "next";
	
	private static final String DEFAULT_PREV_PARAMETER = "prev";
	
	private static final String DEFAULT_SIZE_PARAMETER = "size";
	
	private static final String DEFAULT_DIRECTION_PARAMETER = "direction";
	
	private static final String DEFAULT_PREFIX = "";
	
	private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
	
	private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
	
	static final Chunkable DEFAULT_CHUNK_REQUEST = new ChunkRequest(null, null, DEFAULT_MAX_PAGE_SIZE, null);
	
	
	private static Chunkable getDefaultChunkRequestFrom(MethodParameter parameter) {
		ChunkableDefault defaults = parameter.getParameterAnnotation(ChunkableDefault.class);
		
		if (defaults == null) {
			throw new IllegalArgumentException("MethodParameter must have @ChunkableDefault");
		}
		
		int defaultPageSize = defaults.size();
		if (defaultPageSize == 10) {
			defaultPageSize = defaults.value();
		}
		
		if (defaultPageSize < 1) {
			Method annotatedMethod = parameter.getMethod();
			throw new IllegalStateException(String.format(Locale.ENGLISH, INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
		}
		
		return new ChunkRequest(defaultPageSize, defaults.direction());
	}
	
	
	@NonNull
	@Setter
	@Getter(AccessLevel.PROTECTED)
	private Chunkable fallbackChunkable = DEFAULT_CHUNK_REQUEST;
	
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String nextParameterName = DEFAULT_NEXT_PARAMETER;
	
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String prevParameterName = DEFAULT_PREV_PARAMETER;
	
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String sizeParameterName = DEFAULT_SIZE_PARAMETER;
	
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String directionParameterName = DEFAULT_DIRECTION_PARAMETER;
	
	@Getter(AccessLevel.PROTECTED)
	private String prefix = DEFAULT_PREFIX;
	
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
	
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;
	
	
	/**
	 * Returns whether the given {@link Chunkable} is the fallback one.
	 * 
	 * @param chunkable
	 * @since 0.19
	 * @return
	 */
	public boolean isFallbackChunkable(Chunkable chunkable) {
		return fallbackChunkable.equals(chunkable);
	}
	
	/**
	 * Configures a general prefix to be prepended to the page number and page size parameters. Useful to namespace the
	 * property names used in case they are clashing with ones used by your application. By default, no prefix is used.
	 * 
	 * @param prefix the prefix to be used or {@literal null} to reset to the default.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Chunkable.class.equals(parameter.getParameterType());
	}
	
	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, // NOPMD - cc
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception { // NOPMD - ex
		SpringDataChunkableAnnotationUtils.assertChunkableUniqueness(methodParameter);
		
		String next = webRequest.getParameter(getParameterNameToUse(nextParameterName, methodParameter));
		String prev = webRequest.getParameter(getParameterNameToUse(prevParameterName, methodParameter));
		String pageSizeString = webRequest.getParameter(getParameterNameToUse(sizeParameterName, methodParameter));
		String directionString =
				webRequest.getParameter(getParameterNameToUse(directionParameterName, methodParameter));
		
		Chunkable defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter);
		if (StringUtils.hasText(next) == false
				&& StringUtils.hasText(prev) == false
				&& StringUtils.hasText(pageSizeString) == false
				&& StringUtils.hasText(directionString) == false) {
			return defaultOrFallback;
		}
		
		Integer pageSize = defaultOrFallback.getMaxPageSize() != null
				? defaultOrFallback.getMaxPageSize() : maxPageSize;
		if (StringUtils.hasText(pageSizeString)) {
			try {
				pageSize = Integer.parseInt(pageSizeString);
			} catch (NumberFormatException e) {
				log.trace("invalid page size: {}", pageSizeString);
			}
		}
		
		// Limit lower bound
		pageSize = pageSize < 1 ? 1 : pageSize;
		// Limit upper bound
		pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
		
		Direction direction = Direction.fromOptionalString(directionString).orElse(null);
		
		if (StringUtils.hasText(next)) {
			return new ChunkRequest(next, PaginationRelation.NEXT, pageSize, direction);
		} else if (StringUtils.hasText(prev)) {
			return new ChunkRequest(prev, PaginationRelation.PREV, pageSize, direction);
		} else {
			return new ChunkRequest(pageSize, direction);
		}
		
	}
	
	private Chunkable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {
		if (methodParameter.hasParameterAnnotation(ChunkableDefault.class)) {
			return getDefaultChunkRequestFrom(methodParameter);
		}
		
		return fallbackChunkable;
	}
	
	/**
	 * Returns the name of the request parameter to find the {@link Chunkable} information in. Inspects the given
	 * {@link MethodParameter} for {@link Qualifier} present and prefixes the given source parameter name with it.
	 * 
	 * @param source the basic parameter name.
	 * @param parameter the {@link MethodParameter} potentially qualified.
	 * @return the name of the request parameter.
	 */
	protected String getParameterNameToUse(String source, MethodParameter parameter) {
		StringBuilder builder = new StringBuilder(prefix);
		
		if (parameter != null) {
			Qualifier qualifier = parameter.getParameterAnnotation(Qualifier.class);
			if (qualifier != null) {
				builder.append(qualifier.value());
				builder.append(qualifierDelimiter);
			}
		}
		
		return builder.append(source).toString();
	}
}
