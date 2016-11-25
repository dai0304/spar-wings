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

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Base64;

/**
 * Amazon SNSによる署名の検証を行うクラス。
 * 
 * @since 0.3
 * @author daisuke
 */
public class AmazonSNSMessageVerifier {
	
	private static Logger logger = LoggerFactory.getLogger(AmazonSNSMessageVerifier.class);
	
	private static final String NL = "\n";
	
	
	/**
	 * メッセージ署名の正当性を確認する。
	 * 
	 * @param message 対象メッセージ
	 * @throws NullPointerException 引数に{@code null}を与えた場合
	 * @throws SecurityException
	 * @since 1.0
	 */
	public void verify(SNSMessage message) {
		if (message.getSignatureVersion().equals("1")) {
			// Check the signature and throw an exception if the signature verification fails.
			if (isMessageSignatureValid(message)) {
				logger.trace(">>Signature verification succeeded");
			} else {
				logger.error(">>Signature verification failed");
				throw new SecurityException("Signature verification failed.");
			}
		} else {
			logger.error(">>Unexpected signature version. Unable to verify signature.");
			throw new SecurityException("Unexpected signature version. Unable to verify signature.");
		}
	}
	
	private static boolean isMessageSignatureValid(SNSMessage msg) {
		try (InputStream inStream = new URL(msg.getSigningCertURL()).openStream()) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			logger.trace("Certificate generating...");
			Certificate cert = cf.generateCertificate(inStream);
			logger.trace("Certificate generated");
			
			Signature sig = Signature.getInstance("SHA1withRSA");
			logger.trace("Verification initializing...");
			sig.initVerify(cert.getPublicKey());
			logger.trace("Verification updating...");
			sig.update(getMessageBytesToSign(msg));
			logger.trace("Signature verifing...");
			boolean verify = sig.verify(Base64.decodeBase64(msg.getSignature()));
			logger.trace("Signature verified {}", verify);
			return verify;
		} catch (Exception e) { // NOPMD
			logger.warn("Verification failed.", e);
			throw new SecurityException("Verify method failed.", e);
		}
	}
	
	private static byte[] getMessageBytesToSign(SNSMessage msg) {
		byte[] bytesToSign = null;
		if (msg.getType().equals("Notification")) {
			bytesToSign = buildNotificationStringToSign(msg).getBytes(StandardCharsets.UTF_8);
		} else if (msg.getType().equals("SubscriptionConfirmation")
				|| msg.getType().equals("UnsubscribeConfirmation")) {
			bytesToSign = buildSubscriptionStringToSign(msg).getBytes(StandardCharsets.UTF_8);
		}
		return bytesToSign;
	}
	
	// Build the string to sign for Notification messages.
	static String buildNotificationStringToSign(SNSMessage msg) {
		//Name and values separated by newline characters
		//The name value pairs are sorted by name
		//in byte sort order.
		StringBuilder stringToSign = new StringBuilder(128)
			.append("Message\n").append(msg.getMessage()).append(NL)
			.append("MessageId\n").append(msg.getMessageId()).append(NL);
		if (msg.getSubject() != null) {
			stringToSign.append("Subject\n").append(msg.getSubject()).append(NL);
		}
		stringToSign
			.append("Timestamp\n").append(msg.getTimestamp()).append(NL)
			.append("TopicArn\n").append(msg.getTopicArn()).append(NL)
			.append("Type\n").append(msg.getType()).append(NL);
		
		return stringToSign.toString();
	}
	
	//Build the string to sign for SubscriptionConfirmation and UnsubscribeConfirmation messages.
	static String buildSubscriptionStringToSign(SNSMessage msg) {
		//Build the string to sign from the values in the message.
		//Name and values separated by newline characters
		//The name value pairs are sorted by name
		//in byte sort order.
		StringBuilder stringToSign = new StringBuilder(128)
			.append("Message\n").append(msg.getMessage()).append(NL)
			.append("MessageId\n").append(msg.getMessageId()).append(NL)
			.append("SubscribeURL\n").append(msg.getSubscribeURL()).append(NL)
			.append("Timestamp\n").append(msg.getTimestamp()).append(NL)
			.append("Token\n").append(msg.getToken()).append(NL)
			.append("TopicArn\n").append(msg.getTopicArn()).append(NL)
			.append("Type\n").append(msg.getType()).append(NL);
		
		return stringToSign.toString();
	}
}
