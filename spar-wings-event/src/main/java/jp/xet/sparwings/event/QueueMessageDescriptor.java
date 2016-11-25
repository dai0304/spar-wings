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

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO for daisuke
 * 
 * @since 0.16
 * @author daisuke
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("serial")
public class QueueMessageDescriptor implements Serializable {
	
	@Getter
	@Setter
	@JsonProperty("queue")
	private String queue;
	
	@Getter
	@Setter
	@JsonProperty("message_id")
	private String messageId;
}
