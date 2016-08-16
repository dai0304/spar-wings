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

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@link Filter ServletFilter} implementation that put {@link InstanceInfo} to {@link MDC}.
 * 
 * @since 0.3
 * @author daisuke
 */
public class EC2InstanceInfoLogFilter extends OncePerRequestFilter {
	
	@Autowired
	EC2MetadataUtils.InstanceInfo instanceInfo;
	
	
	@Override
	public void destroy() {
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			registerMDCValues();
			filterChain.doFilter(request, response);
		} finally {
			deregisterMDCValues();
		}
	}
	
	private void registerMDCValues() {
		putIfNotNull("im.instanceId", instanceInfo.getInstanceId());
		putIfNotNull("im.billingProducts", Arrays.toString(instanceInfo.getBillingProducts()));
		putIfNotNull("im.version", instanceInfo.getVersion());
		putIfNotNull("im.imageId", instanceInfo.getImageId());
		putIfNotNull("im.accountId", instanceInfo.getAccountId());
		putIfNotNull("im.instanceType", instanceInfo.getInstanceType());
		putIfNotNull("im.architecture", instanceInfo.getArchitecture());
		putIfNotNull("im.kernelId", instanceInfo.getKernelId());
		putIfNotNull("im.ramdiskId", instanceInfo.getRamdiskId());
		putIfNotNull("im.pendingTime", instanceInfo.getPendingTime());
		putIfNotNull("im.availabilityZone", instanceInfo.getAvailabilityZone());
		putIfNotNull("im.devpayProductCodes", Arrays.toString(instanceInfo.getDevpayProductCodes()));
		putIfNotNull("im.privateIp", instanceInfo.getPrivateIp());
		putIfNotNull("im.region", instanceInfo.getRegion());
	}
	
	private void deregisterMDCValues() {
		MDC.remove("im.instanceId");
		MDC.remove("im.billingProducts");
		MDC.remove("im.version");
		MDC.remove("im.imageId");
		MDC.remove("im.accountId");
		MDC.remove("im.instanceType");
		MDC.remove("im.architecture");
		MDC.remove("im.kernelId");
		MDC.remove("im.ramdiskId");
		MDC.remove("im.pendingTime");
		MDC.remove("im.availabilityZone");
		MDC.remove("im.devpayProductCodes");
		MDC.remove("im.privateIp");
		MDC.remove("im.region");
	}
	
	private void putIfNotNull(String key, String value) {
		if (value != null && value.isEmpty() == false) {
			MDC.put(key, value);
		}
	}
}
