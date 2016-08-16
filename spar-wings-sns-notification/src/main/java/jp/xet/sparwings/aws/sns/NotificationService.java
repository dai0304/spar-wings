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
package jp.xet.sparwings.aws.sns;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import jp.xet.sparwings.spring.env.EnvironmentService;

/**
 * 開発担当者へのイベント通知サービス。
 * 
 * <p>開発担当者に対して各種イベントやエラー・障害等の通知を行うサービス。</p>
 * 
 * @since 0.3
 * @author daisuke
 */
public class NotificationService implements InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(NotificationService.class);
	
	
	/**
	 * Returns stacktrace as string.
	 *
	 * @param t the exception
	 * @return stacktrace
	 * @since 0.1
	 */
	private static String toString(Throwable t) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		
		t.printStackTrace(pw);
		
		return writer.toString();
	}
	
	
	private final String appCodeName;
	
	@Autowired
	AmazonSNS sns;
	
	@Autowired(required = false)
	InstanceInfo instanceInfo;
	
	@Deprecated
	@Autowired(required = false)
	jp.xet.sparwings.aws.ec2.InstanceMetadata instanceMetadata;
	
	@Autowired
	EnvironmentService env;
	
	@Value("#{systemEnvironment['CFN_STACK_NAME'] ?: systemProperties['CFN_STACK_NAME']}")
	String stackName;
	
	@Value("#{systemEnvironment['DEV_TOPIC_ARN'] ?: systemProperties['DEV_TOPIC_ARN']}")
	String devTopicArn;
	
	@Value("#{systemEnvironment['OPS_TOPIC_ARN'] ?: systemProperties['OPS_TOPIC_ARN']}")
	String opsTopicArn;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param appCodeName Application code name
	 * @since 0.3
	 */
	public NotificationService(String appCodeName) {
		this.appCodeName = appCodeName;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Initialize devTopicArn = {}", devTopicArn);
		logger.info("Initialize opsTopicArn = {}", opsTopicArn);
	}
	
	/**
	 * 運用担当者にメッセージを通知する。
	 * 
	 * @param subject タイトル
	 * @param message メッセージ本文
	 * @since 0.3
	 */
	public void notifyOps(String subject, String message) {
		notifyMessage0(opsTopicArn, subject, message);
	}
	
	/**
	 * 開発担当者にメッセージを通知する。
	 * 
	 * @param subject タイトル
	 * @param message メッセージ本文
	 * @since 0.3
	 */
	public void notifyDev(String subject, String message) {
		notifyDev(subject, message, null);
	}
	
	/**
	 * 開発担当者に例外エラーメッセージを通知する。
	 * 
	 * @param subject タイトル
	 * @param message メッセージ本文
	 * @param t 例外
	 * @since 0.3
	 */
	public void notifyDev(String subject, String message, Throwable t) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put("message", message);
		notifyDev(subject, messageMap, t);
	}
	
	/**
	 * 開発担当者に例外エラーメッセージを通知する。
	 * 
	 * @param t 例外
	 * @since 0.3
	 */
	public void notifyDev(Throwable t) {
		notifyDev("unexpected exception", new HashMap<>(), t);
	}
	
	/**
	 * 開発担当者に例外エラーメッセージを通知する。
	 * 
	 * @param message メッセージ本文
	 * @param t 例外
	 * @since 0.3
	 */
	public void notifyDev(String message, Throwable t) {
		Map<String, String> messageMap = new LinkedHashMap<>();
		messageMap.put("message", message);
		notifyDev("unexpected exception", messageMap, t);
	}
	
	/**
	 * 開発担当者にメッセージを通知する。
	 *
	 * @param subject タイトル
	 * @param messageMap メッセージ
	 * @param t 例外
	 * @since 0.3
	 */
	public void notifyDev(String subject, Map<String, String> messageMap, Throwable t) {
		messageMap.put("environment", env.toString());
		messageMap.put("instanceMetadata", Objects.toString(Optional.<Object> ofNullable(instanceInfo)
			.orElse(instanceMetadata)));
		if (t != null) {
			messageMap.put("stackTrace", toString(t));
		}
		
		notifyMessage0(devTopicArn, subject, createMessage(messageMap));
	}
	
	private String createMessage(Map<String, String> messageMap) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> contextMap = MDC.getCopyOfContextMap();
		if (contextMap != null) {
			for (Map.Entry<String, String> e : contextMap.entrySet()) {
				sb.append("MDC-").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
			}
		}
		for (Map.Entry<String, String> e : messageMap.entrySet()) {
			sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
		}
		return sb.toString();
	}
	
	private void notifyMessage0(String topicArn, String subject, String message) {
		subject = String.format("[%s:%s] %s (%s)", appCodeName, stackName, subject, env.getActiveProfilesAsString());
		if (subject.length() > 100) {
			logger.warn("Topic message subject is truncated.  Full subject is: {}", subject);
			subject = subject.substring(0, 100);
		}
		
		logger.debug("notify message to topic[{}] - {} : {}", topicArn, subject, message);
		if (topicArn == null || topicArn.isEmpty() || topicArn.equals("arn:aws:sns:null")) {
			logger.debug("topicArn: NULL");
			return;
		}
		try {
			sns.publish(new PublishRequest()
				.withTopicArn(topicArn)
				.withSubject(subject)
				.withMessage(message));
			logger.debug("SNS Notification published: {} - {}", topicArn, subject);
		} catch (Exception e) {
			logger.error("SNS Publish failed: {} - {} - {}", topicArn, subject, message, e);
		}
	}
}
