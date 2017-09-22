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
package jp.xet.sparwings.aws.sns;

import static org.mockito.Mockito.mock;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import org.junit.Test;

import com.amazonaws.services.sns.AmazonSNS;

import jp.xet.sparwings.spring.env.EnvironmentService;

public class NotificationServiceTest {
	
	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		context.getBean(NotificationService.class);
	}
	
	
	@Configuration
	public static class TestConfiguration {
		
		@Bean
		public PropertySourcesPlaceholderConfigurer propertysourcePlaceholderConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}
		
		@Bean
		public AutowiredAnnotationBeanPostProcessor postProcessor() {
			
			return new AutowiredAnnotationBeanPostProcessor();
		}
		
		@Bean
		public NotificationService notificationService() {
			return new NotificationService(mock(AmazonSNS.class), "sample-app", mock(EnvironmentService.class));
		}
	}
}
