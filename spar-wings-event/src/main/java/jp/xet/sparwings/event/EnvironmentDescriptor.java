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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author daisuke
 */
@Data
@JsonInclude(Include.NON_NULL)
@Accessors(chain = true)
@SuppressWarnings("serial")
public class EnvironmentDescriptor implements Serializable {
	
	/** AWS account ID */
	@Getter
	@Setter
	@JsonProperty("acoount_id")
	private String accountId;
	
	/** AWS region */
	@Getter
	@Setter
	@JsonProperty("region")
	private String region;
	
	/** AWS EC2 instance type */
	@Getter
	@Setter
	@JsonProperty("instance_type")
	private String instanceType;
	
	/** AWS EC2 instance ID */
	@Getter
	@Setter
	@JsonProperty("instance_id")
	private String instanceId;
	
	/** Private IP address */
	@Getter
	@Setter
	@JsonProperty("private_ip")
	private String privateIp;
	
}
