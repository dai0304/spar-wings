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
package jp.xet.sparwings.aws.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;

/**
 * Test for {@link AwsCliConfigFile}.
 * 
 * @since 0.10
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class AwsCliConfigFileTest {
	
	AwsCliConfigFile sut;
	
	
	@Before
	public void setUp() throws Exception {
		sut = new AwsCliConfigFile(new File("./src/test/resources/sample_config"));
	}
	
	@Test
	public void testAllProfiles() {
		// exercise
		Map<String, AwsCliProfile> actual = sut.getAllProfiles();
		// verify
		assertThat(actual.keySet(), hasItems("test", "src"));
	}
	
	@Test
	public void testSrcProfiles() {
		// exercise
		AWSCredentialsProvider actual = sut.getCredentialsProvider("src");
		// verify
		assertThat(actual, is(instanceOf(AWSStaticCredentialsProvider.class)));
		
	}
	
	@Test
	public void testTestProfiles() {
		// exercise
		AWSCredentialsProvider actual = sut.getCredentialsProvider("test");
		// verify
		assertThat(actual, is(instanceOf(STSAssumeRoleSessionCredentialsProvider.class)));
		
	}
}
