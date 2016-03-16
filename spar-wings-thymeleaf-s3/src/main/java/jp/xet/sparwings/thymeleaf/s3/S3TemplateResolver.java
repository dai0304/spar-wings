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

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author Daisuke F.
 */
public class S3TemplateResolver extends TemplateResolver {
	
	/**
	 * Create instance.
	 * 
	 * @param s3ResourceResolver The resolver
	 * @since #version#
	 */
	public S3TemplateResolver(S3TemplateResourceResolver s3ResourceResolver) {
		setResourceResolver(s3ResourceResolver);
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
