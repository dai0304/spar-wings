/*
 * Copyright 2015-2016 Classmethod, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All source code, documentation and other information
 * contained herein is, and remains the property of Classmethod, Inc.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Classmethod, Inc.
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
 * @author Daisuke F.
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
