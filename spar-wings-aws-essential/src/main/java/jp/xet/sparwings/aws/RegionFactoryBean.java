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
package jp.xet.sparwings.aws;

import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import jp.xet.sparwings.aws.ec2.InstanceMetadata;
import lombok.Getter;
import lombok.Setter;

/**
 * Spring factyory bean for {@link Region}.
 *
 * @since 0.3
 * @author daisuke
 * @deprecated use {@link AwsRegionProvider}
 */
@Deprecated
public class RegionFactoryBean implements FactoryBean<Region> {
	
	private static Logger logger = LoggerFactory.getLogger(RegionFactoryBean.class);
	
	@Getter
	@Setter
	private Regions defaultRegion = Regions.US_EAST_1;
	
	@Autowired
	InstanceMetadata metadata;
	
	
	@Override
	public Region getObject() {
		Regions region = defaultRegion;
		for (Regions r : Regions.values()) {
			if (r.getName().equals(metadata.getRegion())) {
				region = r;
				break;
			}
		}
		
		Region bean = Region.getRegion(region);
		logger.info("loaded {}", bean);
		return bean;
	}
	
	@Override
	public Class<?> getObjectType() {
		return Region.class;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
}
