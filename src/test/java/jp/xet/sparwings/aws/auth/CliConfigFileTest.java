/*
 * Copyright 2015 Miyamoto Daisuke.
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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO for daisuke
 * 
 * @since 0.10
 * @version $Id$
 * @author daisuke
 */
public class CliConfigFileTest {
	
	AwsCliConfigFile sut;
	
	
	@Before
	public void setUp() throws Exception {
		File configFile = new File(new File("."), "src/test/resources/sample_config");
		sut = new AwsCliConfigFile(configFile);
	}
	
	@Test
	public void testAllProfiles() {
		// exercise
		Map<String, AwsCliProfile> actual = sut.getAllProfiles();
		// verify
		assertThat(actual.keySet(), hasItems("test", "src"));
		sut.getCredentialsProvider("src");
	}
	
	@Test
	public void testSrcProfiles() {
		// exercise
		AWSCredentialsProvider actual = sut.getCredentialsProvider("src");
		// verify
		assertThat(actual, is(instanceOf(StaticCredentialsProvider.class)));
		
	}
	
	@Test
	public void testTestProfiles() {
		// exercise
		AWSCredentialsProvider actual = sut.getCredentialsProvider("test");
		// verify
		assertThat(actual, is(instanceOf(STSAssumeRoleSessionCredentialsProvider.class)));
		
	}
}
