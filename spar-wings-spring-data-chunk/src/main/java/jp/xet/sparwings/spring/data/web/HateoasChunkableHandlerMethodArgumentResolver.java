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

import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM;
import static org.springframework.hateoas.TemplateVariable.VariableType.REQUEST_PARAM_CONTINUED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.xet.sparwings.spring.data.chunk.Chunkable;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.mvc.UriComponentsContributor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class HateoasChunkableHandlerMethodArgumentResolver
		extends ChunkableHandlerMethodArgumentResolver implements UriComponentsContributor {
	
	/**
	 * Returns the template variable for the pagination parameters.
	 * 
	 * @param parameter can be {@literal null}.
	 * @return
	 * @since 1.7
	 */
	public TemplateVariables getPaginationTemplateVariables(MethodParameter parameter, UriComponents template) {
		
		String eskPropertyName = getParameterNameToUse(getEskParameterName(), parameter);
		String sizePropertyName = getParameterNameToUse(getSizeParameterName(), parameter);
		
		List<TemplateVariable> names = new ArrayList<>();
		MultiValueMap<String, String> queryParameters = template.getQueryParams();
		boolean append = !queryParameters.isEmpty();
		
		for (String propertyName : Arrays.asList(eskPropertyName, sizePropertyName)) {
			if (queryParameters.containsKey(propertyName) == false) {
				VariableType type = append ? REQUEST_PARAM_CONTINUED : REQUEST_PARAM;
				String description = String.format("pagination.%s.description", propertyName);
				names.add(new TemplateVariable(propertyName, type, description));
			}
		}
		
		TemplateVariables pagingVariables = new TemplateVariables(names);
		return pagingVariables;
	}
	
	@Override
	public void enhance(UriComponentsBuilder builder, MethodParameter parameter, Object value) {
		if (value instanceof Chunkable == false) {
			return;
		}
		
		Chunkable chunkable = (Chunkable) value;
		
		String eskPropertyName = getParameterNameToUse(getEskParameterName(), parameter);
		String sizePropertyName = getParameterNameToUse(getSizeParameterName(), parameter);
		
		builder.replaceQueryParam(eskPropertyName, chunkable.getExclusiveStartKey());
		builder.replaceQueryParam(sizePropertyName, chunkable.getMaxPageSize() <= getMaxPageSize()
				? chunkable.getMaxPageSize()
				: getMaxPageSize());
	}
}
