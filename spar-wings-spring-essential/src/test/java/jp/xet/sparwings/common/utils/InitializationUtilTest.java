/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/08/16
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.xet.sparwings.common.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Test for {@link InitializationUtil}.
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("javadoc")
public class InitializationUtilTest {
	
	@Test
	public void testLogAllProperties() {
		// setup
		ch.qos.logback.classic.Logger utilLogger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(InitializationUtil.class);
		@SuppressWarnings("unchecked")
		Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		utilLogger.addAppender(mockAppender);
		
		String nonSecretValue = "test value";
		String secretValue = "secret! secret!";
		System.setProperty("sparwings.test", nonSecretValue);
		System.setProperty("sparwings.test.password", secretValue);
		System.setProperty("sparwings.test.secret", secretValue);
		
		// exercise
		InitializationUtil.logAllProperties();
		
		// verify
		ArgumentCaptor<LoggingEvent> acptor = ArgumentCaptor.forClass(LoggingEvent.class);
		verify(mockAppender, atLeastOnce()).doAppend(acptor.capture());
		List<LoggingEvent> logEvents = acptor.getAllValues();
		
		// regular environment variables
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.startsWith("HOME = /")), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.startsWith("USER = ")), is(true));
		
		// regular system properties
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.startsWith("java.vm.name = ")), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.startsWith("java.home = ")), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.startsWith("user.home = ")), is(true));
		
		// custom system properties
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.equals("sparwings.test = " + nonSecretValue)), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.equals("sparwings.test.password = " + InitializationUtil.MASK_STRING)), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.equals("sparwings.test.secret = " + InitializationUtil.MASK_STRING)), is(true));
		assertThat(logEvents.stream().map(LoggingEvent::getMessage)
			.anyMatch(m -> m.contains(secretValue)), is(false));
		
		// teardown
		System.clearProperty("sparwings.test");
	}
}
