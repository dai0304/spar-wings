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
package jp.xet.sparwings.spring.s3;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;

/**
 * {@link ResourceLoader} implementation to load {@link S3ObjectResource}.
 * 
 * @since 0.12
 * @author daisuke
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class S3ObjectResourceLoader implements ResourceLoader, InitializingBean {
	
	private final AmazonS3 amazonS3;
	
	private final ResourceLoader delegate;
	
	/**
	 * <b>IMPORTANT:</b> If a task executor is set with an unbounded queue there will be a huge memory consumption. The
	 * reason is that each multipart of 5MB will be put in the queue to be uploaded. Therefore a bounded queue is recommended.
	 */
	@Setter
	private TaskExecutor taskExecutor;
	
	
	/**
	 * Create instance with client and class loader.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param classLoader class loader for delegation
	 * @since 0.12
	 */
	public S3ObjectResourceLoader(AmazonS3 amazonS3, ClassLoader classLoader) {
		this.amazonS3 = amazonS3;
		delegate = new DefaultResourceLoader(classLoader);
	}
	
	/**
	 * Create instance with client.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @since 0.12
	 */
	public S3ObjectResourceLoader(AmazonS3 amazonS3) {
		this(amazonS3, ClassUtils.getDefaultClassLoader());
	}
	
	@Override
	public void afterPropertiesSet() {
		if (taskExecutor == null) {
			taskExecutor = new SyncTaskExecutor();
		}
	}
	
	@Override
	public Resource getResource(String location) {
		UriComponents components = UriComponentsBuilder.fromUriString(location).build();
		if (Objects.equals(components.getScheme(), "s3")) {
			String bucketName = components.getHost();
			
			if (bucketName == null) {
				throw new IllegalArgumentException("location must contain host");
			}
			
			String key = components.getPath();
			
			if (key == null) {
				throw new IllegalArgumentException("location must contain path");
			}
			
			if (key.startsWith("/")) {
				key = key.substring(1);
			}
			Optional<String> versionId = Optional.ofNullable(components.getQueryParams())
				.flatMap(queryParams -> queryParams.getOrDefault("versionId", Collections.emptyList()).stream()
					.findFirst());
			return new S3ObjectResource(amazonS3, bucketName, key, versionId, taskExecutor);
		}
		
		// TODO support http or https scheme
		
		return delegate.getResource(location);
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}
}
