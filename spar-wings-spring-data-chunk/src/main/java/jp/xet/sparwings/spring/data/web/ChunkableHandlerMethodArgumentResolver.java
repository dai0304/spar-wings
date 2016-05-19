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

import jp.xet.sparwings.spring.data.chunk.ChunkRequest;
import jp.xet.sparwings.spring.data.chunk.Chunkable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Extracts paging information from web requests and thus allows injecting {@link Chunkable} instances into controller
 * methods. Request properties to be parsed can be configured. Default configuration uses request parameters beginning
 * with {@link #DEFAULT_ESK_PARAMETER}{@link #DEFAULT_QUALIFIER_DELIMITER}.
 * 
 * @since #version#
 * @author daisuke
 */
public class ChunkableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
	
	private static final String INVALID_DEFAULT_PAGE_SIZE =
			"Invalid default page size configured for method %s! Must not be less than one!";
	
	private static final String DEFAULT_ESK_PARAMETER = "esk";
	
	private static final String DEFAULT_SIZE_PARAMETER = "size";
	
	private static final String DEFAULT_DIRECTION_PARAMETER = "direction";
	
	private static final String DEFAULT_PREFIX = "";
	
	private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
	
	private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
	
	static final Chunkable DEFAULT_CHUNK_REQUEST = new ChunkRequest(null, null, null);
	
	
	private static Chunkable getDefaultChunkRequestFrom(MethodParameter parameter) {
		ChunkableDefault defaults = parameter.getParameterAnnotation(ChunkableDefault.class);
		
		String defaultESK = defaults.esk();
		if (defaultESK == ChunkableDefault.DEFAULT_ESK) {
			defaultESK = null;
		}
		Integer defaultPageSize = defaults.size();
		
		if (defaultPageSize < 1) {
			Method annotatedMethod = parameter.getMethod();
			throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
		}
		
		if (defaults.direction() == Direction.ASC) {
			return new ChunkRequest(defaultESK, defaultPageSize);
		}
		
		return new ChunkRequest(defaultESK, defaultPageSize, defaults.direction());
	}
	
	
	@NonNull
	@Setter
	@Getter(AccessLevel.PROTECTED)
	private Chunkable fallbackChunkable = DEFAULT_CHUNK_REQUEST;
	
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	@Setter
	private String eskParameterName = DEFAULT_ESK_PARAMETER;
	
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
	 * @since #version#
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
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		SpringDataChunkableAnnotationUtils.assertChunkableUniqueness(methodParameter);
		
		Chunkable defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter);
		
		String esk = webRequest.getParameter(getParameterNameToUse(eskParameterName, methodParameter));
		String pageSizeString = webRequest.getParameter(getParameterNameToUse(sizeParameterName, methodParameter));
		String directionString =
				webRequest.getParameter(getParameterNameToUse(directionParameterName, methodParameter));
		
		if (StringUtils.hasText(esk) == false
				&& StringUtils.hasText(pageSizeString) == false
				&& StringUtils.hasText(directionString) == false) {
			return defaultOrFallback;
		}
		
		Integer pageSize;
		if (StringUtils.hasText(pageSizeString)) {
			try {
				int parsed = Integer.parseInt(pageSizeString);
				pageSize = parsed < 0 ? 0 : parsed > maxPageSize ? maxPageSize : parsed;
			} catch (NumberFormatException e) {
				pageSize = null;
			}
		} else {
			pageSize = defaultOrFallback.getMaxPageSize();
		}
		if (pageSize != null) {
			// Limit lower bound
			pageSize = pageSize < 1 ? 1 : pageSize;
		}
		if (pageSize != null) {
			// Limit upper bound
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
		}
		
		Direction direction = Direction.fromStringOrNull(directionString);
		
		return new ChunkRequest(esk, pageSize, direction);
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
		
		if (parameter != null && parameter.hasParameterAnnotation(Qualifier.class)) {
			builder.append(parameter.getParameterAnnotation(Qualifier.class).value());
			builder.append(qualifierDelimiter);
		}
		
		return builder.append(source).toString();
	}
}
