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
package jp.xet.sparwings.spring.web.error.attribute;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * TODO for daisuke
 * 
 * <pre><code>
 * public class ... extends WebMvcConfigurerAdapter {
 *   &#64;Bean
 *   &#64;Profile("production")
 *   public ErrorAttributes productionErrorAttributes() {
 *     return new ProductionErrorAttributes();
 *   }
 * }
 * </code></pre>
 * 
 * @since 0.3
 * @author daisuke
 */
public class ProductionErrorAttributes implements ErrorAttributes {
	
	private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
	
	
	@Override
	public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		addStatus(errorAttributes, requestAttributes);
		addErrorDetails(errorAttributes, requestAttributes);
		addPath(errorAttributes, requestAttributes);
		return errorAttributes;
	}
	
	private void addStatus(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		Integer status = getAttribute(requestAttributes, "javax.servlet.error.status_code");
		if (status == null) {
			errorAttributes.put("status", 999);
			errorAttributes.put("error", "None");
			return;
		}
		errorAttributes.put("status", status);
		try {
			errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
		} catch (Exception ex) { // NOPMD
			// Unable to obtain a reason
			errorAttributes.put("error", "Http Status " + status);
		}
	}
	
	private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest requestAttributes) {
		Throwable error = getError(requestAttributes);
		Object message = getAttribute(requestAttributes, "javax.servlet.error.message");
		if ((StringUtils.isEmpty(message) == false || errorAttributes.get("message") == null)
				&& (error instanceof BindingResult) == false) {
			errorAttributes.put("message", StringUtils.isEmpty(message) ? "No message available" : message);
		}
	}
	
	private void addPath(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
		if (path != null) {
			errorAttributes.put("path", path);
		}
	}
	
	@Override
	public Throwable getError(WebRequest requestAttributes) {
		Throwable exception = getAttribute(requestAttributes, ERROR_ATTRIBUTE);
		if (exception == null) {
			exception = getAttribute(requestAttributes, "javax.servlet.error.exception");
		}
		return exception;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}
	
}
