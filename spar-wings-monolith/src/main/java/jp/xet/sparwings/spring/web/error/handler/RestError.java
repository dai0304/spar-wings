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
package jp.xet.sparwings.spring.web.error.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

/**
 * @author Les Hazlewood
 * @since 0.3
 */
@SuppressWarnings("javadoc")
public class RestError {
	
	@Getter
	private final HttpStatus status;
	
	@Getter
	private final int code;
	
	@Getter
	private final String message;
	
	@Getter
	private final String developerMessage;
	
	@Getter
	private final String moreInfoUrl;
	
	@Getter
	private final Throwable throwable;
	
	
	public RestError(HttpStatus status, int code, String message, String developerMessage, String moreInfoUrl,
			Throwable throwable) {
		if (status == null) {
			throw new NullPointerException("HttpStatus argument cannot be null.");
		}
		this.status = status;
		this.code = code;
		this.message = message;
		this.developerMessage = developerMessage;
		this.moreInfoUrl = moreInfoUrl;
		this.throwable = throwable;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof RestError) {
			RestError re = (RestError) o;
			return ObjectUtils.nullSafeEquals(getStatus(), re.getStatus()) &&
					getCode() == re.getCode() &&
					ObjectUtils.nullSafeEquals(getMessage(), re.getMessage()) &&
					ObjectUtils.nullSafeEquals(getDeveloperMessage(), re.getDeveloperMessage()) &&
					ObjectUtils.nullSafeEquals(getMoreInfoUrl(), re.getMoreInfoUrl()) &&
					ObjectUtils.nullSafeEquals(getThrowable(), re.getThrowable());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		//noinspection ThrowableResultOfMethodCallIgnored
		return ObjectUtils.nullSafeHashCode(new Object[] {
			getStatus(),
			getCode(),
			getMessage(),
			getDeveloperMessage(),
			getMoreInfoUrl(),
			getThrowable()
		});
	}
	
	@Override
	public String toString() {
		//noinspection StringBufferReplaceableByString
		return new StringBuilder().append(getStatus().value())
			.append(" (").append(getStatus().getReasonPhrase()).append(" )")
			.toString();
	}
	
	
	@Accessors(chain = true)
	@NoArgsConstructor
	public static class Builder {
		
		private HttpStatus status;
		
		@Setter
		private int code;
		
		@Setter
		private String message;
		
		@Setter
		private String developerMessage;
		
		@Setter
		private String moreInfoUrl;
		
		@Setter
		private Throwable throwable;
		
		
		public Builder setStatus(int statusCode) {
			status = HttpStatus.valueOf(statusCode);
			return this;
		}
		
		public void setStatus(HttpStatus status) {
			this.status = status;
		}
		
		public RestError build() {
			if (status == null) {
				setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new RestError(status, code, message, developerMessage, moreInfoUrl, throwable);
		}
	}
}
