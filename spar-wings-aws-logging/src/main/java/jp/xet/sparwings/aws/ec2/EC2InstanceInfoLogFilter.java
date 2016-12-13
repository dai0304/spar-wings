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

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;

/**
 * {@link Filter ServletFilter} implementation that put {@link InstanceInfo} to {@link MDC}.
 * 
 * @since 0.3
 * @author daisuke
 */
public class EC2InstanceInfoLogFilter extends OncePerRequestFilter {
	
	@Autowired
	EC2MetadataUtils.InstanceInfo instanceInfo;
	
	@Setter
	@Getter
	private String prefix = "im_";
	
	
	@Override
	public void destroy() {
		// nothing to do
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
		if (instanceInfo == null) {
			return;
		}
		putIfNotNull(prefix + "instanceId", instanceInfo.getInstanceId());
		putIfNotNull(prefix + "billingProducts", Arrays.toString(instanceInfo.getBillingProducts()));
		putIfNotNull(prefix + "version", instanceInfo.getVersion());
		putIfNotNull(prefix + "imageId", instanceInfo.getImageId());
		putIfNotNull(prefix + "accountId", instanceInfo.getAccountId());
		putIfNotNull(prefix + "instanceType", instanceInfo.getInstanceType());
		putIfNotNull(prefix + "architecture", instanceInfo.getArchitecture());
		putIfNotNull(prefix + "kernelId", instanceInfo.getKernelId());
		putIfNotNull(prefix + "ramdiskId", instanceInfo.getRamdiskId());
		putIfNotNull(prefix + "pendingTime", instanceInfo.getPendingTime());
		putIfNotNull(prefix + "availabilityZone", instanceInfo.getAvailabilityZone());
		putIfNotNull(prefix + "devpayProductCodes", Arrays.toString(instanceInfo.getDevpayProductCodes()));
		putIfNotNull(prefix + "privateIp", instanceInfo.getPrivateIp());
		putIfNotNull(prefix + "region", instanceInfo.getRegion());
	}
	
	private void deregisterMDCValues() {
		MDC.remove(prefix + "instanceId");
		MDC.remove(prefix + "billingProducts");
		MDC.remove(prefix + "version");
		MDC.remove(prefix + "imageId");
		MDC.remove(prefix + "accountId");
		MDC.remove(prefix + "instanceType");
		MDC.remove(prefix + "architecture");
		MDC.remove(prefix + "kernelId");
		MDC.remove(prefix + "ramdiskId");
		MDC.remove(prefix + "pendingTime");
		MDC.remove(prefix + "availabilityZone");
		MDC.remove(prefix + "devpayProductCodes");
		MDC.remove(prefix + "privateIp");
		MDC.remove(prefix + "region");
	}
	
	private void putIfNotNull(String key, String value) {
		if (value != null && value.isEmpty() == false) {
			MDC.put(key, value);
		}
	}
}
