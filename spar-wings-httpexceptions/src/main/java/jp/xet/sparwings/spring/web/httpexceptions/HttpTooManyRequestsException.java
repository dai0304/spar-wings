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
package jp.xet.sparwings.spring.web.httpexceptions;

import java.util.Locale;

import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TODO for daisuke
 * 
 * @since 0.8
 * @author daisuke
 */
@NoArgsConstructor
@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class HttpTooManyRequestsException extends HttpResponseException {
	
	/**
	 * Create instance with milleseconds to wait.
	 * 
	 * @param millisecsToWait 要待機ミリ秒
	 */
	public HttpTooManyRequestsException(long millisecsToWait) {
		super(String.format(Locale.ENGLISH, "Please wait %d ms before next request", millisecsToWait));
	}
	
	/**
	 * Create instance with detailed message and cause.
	 * 
	 * @param message exception message
	 * @param cause cause of exception
	 * @since 0.26
	 */
	public HttpTooManyRequestsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Create instance with detailed message.
	 * 
	 * @param message exception message
	 * @since 0.26
	 */
	public HttpTooManyRequestsException(String message) {
		super(message);
	}
	
	/**
	 * Create instance with cause.
	 * 
	 * @param cause cause of exception
	 * @since 0.26
	 */
	public HttpTooManyRequestsException(Throwable cause) {
		super(cause);
	}
}
