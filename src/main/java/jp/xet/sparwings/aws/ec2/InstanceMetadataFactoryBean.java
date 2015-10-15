/*
 * Copyright 2015 Miyamoto Daisuke.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring factory bean for {@link InstanceMetadata}.
 *
 * @since 0.3
 * @author daisuke
 */
public class InstanceMetadataFactoryBean implements FactoryBean<InstanceMetadata> {
	
	private static Logger logger = LoggerFactory.getLogger(InstanceMetadataFactoryBean.class);
	
	private static final String USER_DATA_URL = "http://169.254.169.254/latest/dynamic/instance-identity/document";
	
	private static final int TIMEOUT = 1000;
	
	
	@Override
	public InstanceMetadata getObject() {
		ObjectMapper mapper = new ObjectMapper();
		InstanceMetadata metadata = null;
		try (Reader reader = createUserDataReader()) {
			metadata = mapper.readValue(reader, InstanceMetadata.class);
			return metadata;
		} catch (MalformedURLException e) {
			throw new Error(e);
		} catch (IOException e) {
			logger.warn("IOException {}", e.getMessage());
		} catch (Exception e) {
			logger.error("Exception {}", e.getMessage());
		}
		
		if (metadata == null) {
			metadata = new InstanceMetadata();
			metadata.setInstanceId("LOCAL");
		}
		
		logger.info("loaded {}", metadata);
		return metadata;
	}
	
	private Reader createUserDataReader() throws IOException, MalformedURLException {
		URLConnection conn = new URL(USER_DATA_URL).openConnection();
		conn.setReadTimeout(TIMEOUT);
		conn.setConnectTimeout(TIMEOUT);
		conn.connect();
		return new BufferedReader(new InputStreamReader(conn.getInputStream()));
	}
	
	@Override
	public Class<?> getObjectType() {
		return InstanceMetadata.class;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
}
