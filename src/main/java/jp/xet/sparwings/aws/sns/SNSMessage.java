/*
 * Copyright 2015 Miyamoto Daisuke, Inc.
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

import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SNS/SQSから受信したメッセージを表すクラス。
 * 
 * @since #version#
 * @author daisuke
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SNSMessage {
	
	@JsonProperty(value = "Type", required = true)
	private String type;
	
	@JsonProperty(value = "TopicArn", required = true)
	private String topicArn;
	
	@JsonProperty(value = "Timestamp", required = true)
	private String timestamp;
	
	@JsonProperty(value = "SignatureVersion", required = true)
	private String signatureVersion;
	
	@JsonProperty(value = "Signature", required = true)
	private String signature;
	
	@JsonProperty(value = "SigningCertURL", required = true)
	private String signingCertURL;
	
	@JsonProperty("MessageId")
	private String messageId;
	
	@JsonProperty("Subject")
	private String subject;
	
	@JsonProperty("Message")
	private String message;
	
	@JsonProperty("UnsubscribeURL")
	private String unsubscribeURL;
	
	@JsonProperty("SubscribeURL")
	private String subscribeURL;
	
	@JsonProperty("Token")
	private String token;
	
	
	/**
	 * メッセージのタイプを返す。
	 * 
	 * <ul>
	 *   <li>SubscriptionConfirmation</li>
	 *   <li>Notification</li>
	 *   <li>UnsubscribeConfirmation</li>
	 * </ul>
	 * 
	 * @return メッセージのタイプ
	 * @since #version#
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * メッセージのタイプを設定する。
	 * 
	 * @param type メッセージのタイプ
	 * @since #version#
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * メッセージ発生元のSNSトピックARNを返す。
	 * 
	 * @return SNSトピックARN
	 * @since #version#
	 */
	public String getTopicArn() {
		return topicArn;
	}
	
	/**
	 * メッセージ発生元の AMazon SNS トピックARNを設定する。
	 * 
	 * @param topicArn SNSトピックARN
	 * @since #version#
	 */
	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}
	
	/**
	 * メッセージが送信された日時を返す。
	 * 
	 * @return メッセージが送信された日時
	 * @since #version#
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/**
	 * メッセージが送信された日時を設定する。
	 * 
	 * @param timestamp メッセージが送信された日時
	 * @since #version#
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * 使用される Amazon SNS 署名のバージョンを返す。
	 * 
	 * @return 使用される Amazon SNS 署名のバージョン
	 * @since #version#
	 */
	public String getSignatureVersion() {
		return signatureVersion;
	}
	
	/**
	 * 使用される Amazon SNS 署名のバージョンを設定する。
	 * 
	 * @param signatureVersion 使用される Amazon SNS 署名のバージョン
	 * @since #version#
	 */
	public void setSignatureVersion(String signatureVersion) {
		this.signatureVersion = signatureVersion;
	}
	
	/**
	 * このメッセージに対する電子署名を返す。
	 * 
	 * @return 電子署名
	 * @since #version#
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * このメッセージに対する電子署名を設定する。
	 * 
	 * @param signature このメッセージに対する電子署名
	 * @since #version#
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	/**
	 * メッセージの署名に使用された証明書の URL を返す。
	 * 
	 * @return メッセージの署名に使用された証明書の URL
	 * @since #version#
	 */
	public String getSigningCertURL() {
		return signingCertURL;
	}
	
	/**
	 * メッセージの署名に使用された証明書の URL を設定する。
	 * 
	 * @param signingCertURL メッセージの署名に使用された証明書の URL
	 * @since #version#
	 */
	public void setSigningCertURL(String signingCertURL) {
		this.signingCertURL = signingCertURL;
	}
	
	/**
	 * 通知がトピックに公開されたときに指定された Subject パラメータを返す。
	 * 
	 * @return 通知がトピックに公開されたときに指定された Subject パラメータ
	 * @since #version#
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * 通知がトピックに公開されたときに指定された Subject パラメータを設定する。
	 * 
	 * @param subject 通知がトピックに公開されたときに指定された Subject パラメータ
	 * @since #version#
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * 共通のユニークな識別子を返す。。
	 * 
	 * <p>発行される各メッセージで一意です。再試行間に Amazon SNS が再送信する通知の場合、元のメッセージのメッセージ ID が使用されます。</p>
	 * 
	 * @return 共通のユニークな識別子
	 * @since #version#
	 */
	public String getMessageId() {
		return messageId;
	}
	
	/**
	 * 共通のユニークな識別子を設定する。
	 * 
	 * @param messageId 共通のユニークな識別子
	 * @since #version#
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	/**
	 * 通知がトピックに発行されたときに指定された Message の値を返す。
	 * 
	 * @return 通知がトピックに発行されたときに指定された Message の値
	 * @since #version#
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 通知がトピックに発行されたときに指定された Message の値を設定する。
	 * 
	 * @param message 通知がトピックに発行されたときに指定された Message の値
	 * @since #version#
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * このトピックからエンドポイントの受信登録を解除するために使用できる URL を返す。
	 * 
	 * <p>この URL にアクセスすると、Amazon SNS はエンドポイントの受信登録を解除し、このエンドポイントへの通知の送信を停止します。</p>
	 * 
	 * @return このトピックからエンドポイントの受信登録を解除するために使用できる URL
	 * @since #version#
	 */
	public String getUnsubscribeURL() {
		return unsubscribeURL;
	}
	
	/**
	 * このトピックからエンドポイントの受信登録を解除するために使用できる URL を設定する。
	 * 
	 * @param unsubscribeURL このトピックからエンドポイントの受信登録を解除するために使用できる URL
	 * @since 1.1
	 */
	public void setUnsubscribeURL(String unsubscribeURL) {
		this.unsubscribeURL = unsubscribeURL;
	}
	
	/**
	 * 受信登録を再確認するためにアクセスする必要がある URL を返す。
	 * 
	 * @return 受信登録を再確認するためにアクセスする必要がある URL
	 * @since #version#
	 */
	public String getSubscribeURL() {
		return subscribeURL;
	}
	
	/**
	 * 受信登録を再確認するためにアクセスする必要がある URL を設定する。
	 * 
	 * @param subscribeURL 受信登録を再確認するためにアクセスする必要がある URL
	 * @since #version#
	 */
	public void setSubscribeURL(String subscribeURL) {
		this.subscribeURL = subscribeURL;
	}
	
	/**
	 * 受信登録を再確認するために ConfirmSubscription アクションで使用できる値を返す。
	 * 
	 * @return 受信登録を再確認するために ConfirmSubscription アクションで使用できる値
	 * @since #version#
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * 受信登録を再確認するために ConfirmSubscription アクションで使用できる値を設定する。
	 * 
	 * @param token 受信登録を再確認するために ConfirmSubscription アクションで使用できる値
	 * @since #version#
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
/*

{
  "Type" : "Notification",
  "MessageId" : "b42b1281-b563-507e-b750-5e425f5aca9c",
  "TopicArn" : "arn:aws:sns:ap-northeast-1:695874969485:portnoydev-BrianTopic-1RWRIUYD2AL34",
  "Message" : "{\n  \"jobData\": {\n    \"batchJobName\": \"cmBillingAggregationJob\",\n    \"batchJobParameters\": {\n      \"targetMonth\": \"'2014-08'\"\n    }\n  }\n}\n",
  "Timestamp" : "2014-10-24T05:47:37.616Z",
  "SignatureVersion" : "1",
  "Signature" : "QauTr7KgYJgZG04q4XAWJLLYz34cdL/zpMn165ZLB6ERdOMjAvi0152ZxYdzAG52EiWreH51ZsXb9Db5jpAOW8oi1cgvNGsE6FqFBS7EAjoA0DZSqfNP8T8iXQ0JFicsfdPHU/zHVSdHk/UGF2BGvapGE2SRT0Iih4YC4xDYLKfU8091c3wi+KOdUxwx3Xx0aN2U/wW+ML+6I4RzKxZvfirWkHFHXgd68JTLmmc7pCDaJ4pQMxW56ZbbLbRbCqcl72A0O+3kdm7MEjqzWcTVDKXyBIL6D86NUreaLh5iM5ROtLz7o1sIEHMKRZuO/gIZjg+NNGiytLX1oyk/hPNdmA==",
  "SigningCertURL" : "https://sns.ap-northeast-1.amazonaws.com/SimpleNotificationService-d6d679a1d18e95c2f9ffcf11f4f9e198.pem",
  "UnsubscribeURL" : "https://sns.ap-northeast-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:ap-northeast-1:695874969485:portnoydev-BrianTopic-1RWRIUYD2AL34:ec96820e-5a5b-4e49-b3a6-f83050936471",
  "MessageAttributes" : {
    "AWS.SNS.MOBILE.MPNS.Type" : {"Type":"String","Value":"token"},
    "AWS.SNS.MOBILE.WNS.Type" : {"Type":"String","Value":"wns/badge"},
    "AWS.SNS.MOBILE.MPNS.NotificationClass" : {"Type":"String","Value":"realtime"}
  }
}
*/
