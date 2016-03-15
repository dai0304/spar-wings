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
package jp.xet.sparwings.spring.web.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TODO for daisuke
 * 
 * @since 0.9
 * @author daisuke
 */
@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.FORBIDDEN)
public class HttpForbiddenException extends HttpResponseException {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @since 0.9
	 */
	public HttpForbiddenException() {
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param message 例外メッセージ
	 * @param cause 起因例外
	 * @since 0.9
	 */
	public HttpForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param message 例外メッセージ
	 * @since 0.9
	 */
	public HttpForbiddenException(String message) {
		super(message);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param cause 起因例外
	 * @since 0.9
	 */
	public HttpForbiddenException(Throwable cause) {
		super(cause);
	}
}
