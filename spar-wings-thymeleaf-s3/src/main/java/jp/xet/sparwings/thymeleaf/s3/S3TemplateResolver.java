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
package jp.xet.sparwings.thymeleaf.s3;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.amazonaws.services.s3.AmazonS3;

/**
 * TODO for daisuke
 * 
 * @since 0.11
 * @version $Id$
 * @author fd0
 */
public class S3TemplateResolver extends TemplateResolver {
	
	/**
	 * Create instance.
	 * 
	 * @param s3ResourceResolver The resolver
	 * @since 0.11
	 */
	public S3TemplateResolver(S3TemplateResourceResolver s3ResourceResolver) {
		setResourceResolver(s3ResourceResolver);
	}
	
	/**
	 * Create instance.
	 * 
	 * @param s3 The Amazon S3 client
	 * @param bucketName The bucket name for template
	 * @since 0.12
	 */
	public S3TemplateResolver(AmazonS3 s3, String bucketName) {
		this(new S3TemplateResourceResolver(s3, bucketName));
	}
	
	@Override
	protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
		String prefix = getPrefix();
		if (prefix.startsWith("/")) {
			prefix = prefix.substring(1);
		}
		if (prefix.endsWith("/") == false) {
			prefix = prefix + "/";
		}
		return prefix + templateProcessingParameters.getTemplateName() + getSuffix();
	}
}
