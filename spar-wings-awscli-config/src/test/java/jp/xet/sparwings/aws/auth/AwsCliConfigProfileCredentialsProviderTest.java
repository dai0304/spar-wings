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
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO for daisuke
 * 
 * @since 0.10
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class AwsCliConfigProfileCredentialsProviderTest {
	
	private static Logger logger = LoggerFactory.getLogger(AwsCliConfigProfileCredentialsProviderTest.class);
	
	@Mock
	AwsCliConfigFile configFile;
	
	@Mock
	AWSCredentialsProvider cp;
	
	AwsCliConfigProfileCredentialsProvider sut;
	
	
	@Before
	public void setup() throws Exception {
		when(configFile.getCredentialsProvider(eq("test"))).thenReturn(cp);
		sut = new AwsCliConfigProfileCredentialsProvider(configFile, "test");
	}
	
	@Test
	public void test() {
		// setup
		AWSCredentials expectedAwsCredentials = mock(AWSCredentials.class);
		when(cp.getCredentials()).thenReturn(expectedAwsCredentials);
		// exercise
		AWSCredentials actual = sut.getCredentials();
		// verify
		assertThat(actual, is(expectedAwsCredentials));
		logger.info("{}", actual);
	}
}
