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
package jp.xet.sparwings.aws.sqs;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.OverLimitException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;

/**
 * TODO for daisuke
 * 
 * @since 0.3
 * @version $Id$
 * @author daisuke
 */
@RequiredArgsConstructor
public class SqsMessagePoller {
	
	private static Logger logger = LoggerFactory.getLogger(SqsMessagePoller.class);
	
	@Getter
	private final AmazonSQS sqs;
	
	@Getter
	private final RetryTemplate retry;
	
	@Getter
	private final String workerQueueUrl;
	
	@Getter
	private final SqsMessageHandler messageHandler;
	
	@Getter
	@Setter
	private ExecutorService executor = Executors.newCachedThreadPool(r -> {
		Thread thread = new Thread(r);
		thread.setUncaughtExceptionHandler((t, e) -> {
			synchronized (SqsMessagePoller.class) {
				logger.error("Uncaught exception in thread '{}': {}", t.getName(), e.getMessage());
			}
		});
		return thread;
	});
	
	@Getter
	@Setter
	private int visibilityTimeout = 300;
	
	@Getter
	@Setter
	private int changeVisibilityThreshold = 30;
	
	@Getter
	@Setter
	private int waitTimeSeconds = 20;
	
	@Getter
	@Setter
	private int maxNumberOfMessages = 10;
	
	
	/**
	 * TODO for daisuke
	 * 
	 * @since 0.3
	 */
	@Scheduled(fixedDelay = 1)
	public void loop() {
		ReceiveMessageResult receiveMessageResult;
		try {
			logger.trace("Start SQS long polling");
			receiveMessageResult = sqs.receiveMessage(new ReceiveMessageRequest(workerQueueUrl)
				.withWaitTimeSeconds(waitTimeSeconds)
				.withMaxNumberOfMessages(maxNumberOfMessages)
				.withVisibilityTimeout(visibilityTimeout)
				.withAttributeNames("ApproximateReceiveCount"));
		} catch (OverLimitException e) {
			logger.error("SQS over limit", e);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("interrupted", e1);
				throw new Error(e1);
			}
			return;
		}
		
		List<Message> messages = receiveMessageResult.getMessages();
		if (messages.size() == 0) {
			logger.trace("No SQS message received");
			return;
		}
		logger.debug("{} SQS messages are received", messages.size());
		messages.stream().parallel().forEach(message -> {
			logger.info("SQS message was recieved: {}", message.getMessageId());
			logger.debug("Receive SQS:{} C:{} RHD:{}", new Object[] {
				message.getMessageId(),
				message.getAttributes().get("ApproximateReceiveCount"),
				DigestUtils.md5DigestAsHex(message.getReceiptHandle().getBytes())
			});
			
			Future<Void> future = executor.submit(() -> messageHandler.handle(message));
			logger.debug("Main task for {} is submitted", message.getMessageId());
			
			logger.debug("Start visibility timeout follow-up task for {}", message.getMessageId());
			try {
				retry.execute(context -> {
					try {
						future.get(changeVisibilityThreshold, TimeUnit.SECONDS);
						logger.debug("Job for SQS:{} was done", message.getMessageId());
						sqs.deleteMessage(new DeleteMessageRequest(workerQueueUrl, message.getReceiptHandle()));
						logger.info("SQS:{} was deleted", message.getMessageId());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						logger.warn("Job for SQS:{} was interrupted", message.getMessageId());
					} catch (ExecutionException e) { // handle e.getCause()
						logger.error("Job for SQS:{} was failed", message.getMessageId(), e.getCause());
					} catch (TimeoutException e) { // we need more time
						logger.debug("Job for SQS:{} was timeout RHD:{}", new Object[] {
							message.getMessageId(),
							DigestUtils.md5DigestAsHex(message.getReceiptHandle().getBytes())
						});
						sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest(
								workerQueueUrl, message.getReceiptHandle(), visibilityTimeout));
						if (logger.isDebugEnabled()) {
							logger.debug("Visibility for SQS:{} was updated", new Object[] {
								message.getMessageId(),
								visibilityTimeout
							});
						} else if (logger.isTraceEnabled()) {
							logger.trace("Visibility for SQS:{} was updated VT:{} RHD:{}", new Object[] {
								message.getMessageId(),
								visibilityTimeout,
								DigestUtils.md5DigestAsHex(message.getReceiptHandle().getBytes())
							});
						}
						throw e;
					}
					return null;
				});
			} catch (Exception e) {
				logger.error("Retry attempt exceeded?", e);
			}
			logger.debug("Visibility timeout follow-up task for {} was finished", message.getMessageId());
		});
	}
}
