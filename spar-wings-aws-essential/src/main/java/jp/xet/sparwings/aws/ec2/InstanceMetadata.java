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
package jp.xet.sparwings.aws.ec2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * EC2 Instance Metadata.
 * 
 * @since 0.3
 * @author daisuke
 */
@ToString
public class InstanceMetadata {
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private String instanceId;
	
	@Getter
	private String billingProducts;
	
	@Getter
	private String version;
	
	@Getter
	private String imageId;
	
	@Getter
	private String accountId;
	
	@Getter
	private String instanceType;
	
	@Getter
	private String architecture;
	
	@Getter
	private String kernelId;
	
	@Getter
	private String ramdiskId;
	
	@Getter
	private String pendingTime;
	
	@Getter
	private String availabilityZone;
	
	@Getter
	private String devpayProductCodes;
	
	@Getter
	private String privateIp;
	
	@Getter
	private String region;
	
}
