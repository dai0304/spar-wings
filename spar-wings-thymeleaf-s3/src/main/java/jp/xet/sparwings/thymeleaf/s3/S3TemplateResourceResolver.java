/*
 * Copyright 2015-2016 Miyamoto Daisuke.
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

import java.io.InputStream;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

/**
 * {@link IResourceResolver} implementation to get S3 object resource.
 * 
 * @since 0.8
 * @version $Id$
 * @author fd0
 */
@RequiredArgsConstructor
public class S3TemplateResourceResolver implements IResourceResolver {
	
	private static Logger logger = LoggerFactory.getLogger(S3TemplateResourceResolver.class);
	
	private final AmazonS3 s3;
	
	private final String bucketName;
	
	
	@Override
	public String getName() {
		return "S3TemplateResourceResolver";
	}
	
	@Override
	public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters,
			String resourceName) {
		try {
			return s3.getObject(bucketName, resourceName).getObjectContent();
		} catch (AmazonS3Exception e) {
			if (Objects.equals(e.getErrorCode(), "NoSuchKey")) {
				logger.trace(e.getMessage());
			} else {
				logger.warn(e.getMessage());
			}
			return null;
		}
	}
}
