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
package jp.xet.sparwings.aws.ec2;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@link InstanceMetadata}をMDCに出力するための{@link Filter ServletFilter} 実装クラス。
 * 
 * @since 0.3
 * @author daisuke
 */
public class InstanceMetadataLogFilter extends OncePerRequestFilter {
	
	@Autowired
	InstanceMetadata instanceMetadata;
	
	
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
		putIfNotNull("im.instanceId", instanceMetadata.getInstanceId());
		putIfNotNull("im.billingProducts", instanceMetadata.getBillingProducts());
		putIfNotNull("im.version", instanceMetadata.getVersion());
		putIfNotNull("im.imageId", instanceMetadata.getImageId());
		putIfNotNull("im.accountId", instanceMetadata.getAccountId());
		putIfNotNull("im.instanceType", instanceMetadata.getInstanceType());
		putIfNotNull("im.architecture", instanceMetadata.getArchitecture());
		putIfNotNull("im.kernelId", instanceMetadata.getKernelId());
		putIfNotNull("im.ramdiskId", instanceMetadata.getRamdiskId());
		putIfNotNull("im.pendingTime", instanceMetadata.getPendingTime());
		putIfNotNull("im.availabilityZone", instanceMetadata.getAvailabilityZone());
		putIfNotNull("im.devpayProductCodes", instanceMetadata.getDevpayProductCodes());
		putIfNotNull("im.privateIp", instanceMetadata.getPrivateIp());
		putIfNotNull("im.region", instanceMetadata.getRegion());
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
