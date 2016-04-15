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
package jp.xet.sparwings.event;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.context.ApplicationEvent;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author daisuke
 */
@ToString
@JsonInclude(Include.NON_NULL)
@Accessors(chain = true)
@SuppressWarnings("serial")
@JsonPropertyOrder({
	"timestamp",
	"event_type",
	"source",
	"authentication",
	"http_request_trigger",
	"queue_message_trigger"
})
public class SWEvent extends ApplicationEvent {
	
	/** Event type name */
	@Getter
	@JsonProperty("event_type")
	private final String eventType;
	
	/** Trigger authentication */
	@Getter
	@Setter
	@JsonProperty("authentication")
	private AuthenticationDescriptor authentication;
	
	/** HTTP request which publish this event */
	@Getter
	@Setter
	@JsonProperty("request")
	private HttpRequestDescriptor httpRequest;
	
	/** Queue message which publish this event */
	@Getter
	@Setter
	@JsonProperty("message")
	private QueueMessageDescriptor queueMessage;
	
	@JsonIgnore
	private Map<String, Object> map = new LinkedHashMap<>();
	
	
	/**
	 * Create instance.
	 * 
	 * @param source The event source
	 * @param eventType The event type
	 * @since #version#
	 */
	public SWEvent(EventSourceDescriptor source, String eventType) {
		super(source);
		this.eventType = eventType;
	}
	
	@JsonCreator
	SWEvent(@JsonProperty("source") EventSourceDescriptor source,
			@JsonProperty("timestamp") long timestamp,
			@JsonProperty("event_type") String eventType) {
		super(source);
		this.eventType = eventType;
		
		try {
			Field f = ApplicationEvent.class.getDeclaredField("timestamp");
			f.setAccessible(true);
			f.set(this, timestamp);
		} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
			throw new Error(e);
		}
	}
	
	/**
	 * Add any key-value.
	 * 
	 * @param key The key string
	 * @param value The value object
	 * @return {@code this} for method chaining
	 * @since #version#
	 */
	public SWEvent with(String key, Object value) {
		map.put(key, value);
		return this;
	}
	
	/**
	 * Get value by key.
	 * 
	 * @param key The key string
	 * @return The value
	 * @since #version#
	 */
	public Object get(String key) {
		return map.get(key);
	}
	
	@JsonAnySetter
	@SuppressWarnings("javadoc")
	public void set(String key, Object value) {
		map.put(key, value);
	}
	
	@JsonAnyGetter
	@SuppressWarnings("javadoc")
	public Map<String, Object> any() {
		return map;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authentication == null) ? 0 : authentication.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
		result = prime * result + ((httpRequest == null) ? 0 : httpRequest.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((queueMessage == null) ? 0 : queueMessage.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SWEvent other = (SWEvent) obj;
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		if (getTimestamp() != other.getTimestamp()) {
			return false;
		}
		if (authentication == null) {
			if (other.authentication != null) {
				return false;
			}
		} else if (!authentication.equals(other.authentication)) {
			return false;
		}
		if (eventType == null) {
			if (other.eventType != null) {
				return false;
			}
		} else if (!eventType.equals(other.eventType)) {
			return false;
		}
		if (httpRequest == null) {
			if (other.httpRequest != null) {
				return false;
			}
		} else if (!httpRequest.equals(other.httpRequest)) {
			return false;
		}
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		if (queueMessage == null) {
			if (other.queueMessage != null) {
				return false;
			}
		} else if (!queueMessage.equals(other.queueMessage)) {
			return false;
		}
		return true;
	}
}
