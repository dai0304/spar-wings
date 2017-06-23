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
package jp.xet.sparwings.aws.sqs;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.OverLimitException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

/**
 * TODO for daisuke
 * 
 * @since 0.3
 * @version $Id$
 * @author daisuke
 */
@Slf4j
@RequiredArgsConstructor
public class SqsMessagePoller { // NOPMD - cc
	
	@Getter
	private final AmazonSQS sqs;
	
	@Getter
	private final RetryTemplate retry;
	
	@Getter
	private final String workerQueueUrl;
	
	@Getter
	private final Consumer<Message> messageHandler;
	
	@Getter
	@Setter
	private ExecutorService executor = Executors.newCachedThreadPool(r -> {
		Thread thread = new Thread(r);
		thread.setUncaughtExceptionHandler((t, e) -> {
			synchronized (SqsMessagePoller.class) {
				log.error("Uncaught exception in thread '{}': {}", t.getName(), e.getMessage());
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
	@Scheduled(fixedDelay = 1) // SUPPRESS CHECKSTYLE bug?
	public void loop() { // NOPMD - cc
		List<Message> messages = reveiveMessages();
		if (messages.isEmpty()) {
			log.trace("No SQS message received");
			return;
		}
		log.debug("{} SQS messages are received", messages.size());
		messages.stream().parallel().forEach(this::handleMessage);
	}
	
	private List<Message> reveiveMessages() {
		ReceiveMessageResult receiveMessageResult;
		try {
			log.trace("Start SQS long polling");
			receiveMessageResult = sqs.receiveMessage(new ReceiveMessageRequest(workerQueueUrl)
				.withWaitTimeSeconds(waitTimeSeconds)
				.withMaxNumberOfMessages(maxNumberOfMessages)
				.withVisibilityTimeout(visibilityTimeout)
				.withAttributeNames("ApproximateReceiveCount"));
			return receiveMessageResult.getMessages();
		} catch (OverLimitException e) {
			log.error("SQS over limit", e);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				log.error("interrupted", e1);
				throw new AssertionError(e1); // NOPMD - lost OverLimitException's stacktrace
			}
		}
		return Collections.emptyList();
	}
	
	private void handleMessage(Message message) {
		log.info("SQS message was recieved: {}", message.getMessageId());
		log.debug("Receive SQS:{} C:{} RHD:{}",
				message.getMessageId(),
				message.getAttributes().get("ApproximateReceiveCount"),
				computeReceptHandleDigest(message));
		
		Future<Message> future = executor.submit(() -> messageHandler.accept(message), message);
		log.debug("Main task for {} is submitted", message.getMessageId());
		
		doFollowup(message, future);
	}
	
	private void doFollowup(Message message, Future<Message> future) {
		log.debug("Start visibility timeout follow-up task for {}", message.getMessageId());
		try {
			retry.execute(context -> {
				try {
					future.get(changeVisibilityThreshold, TimeUnit.SECONDS);
					log.debug("Job for SQS:{} was done", message.getMessageId());
					sqs.deleteMessage(new DeleteMessageRequest(workerQueueUrl, message.getReceiptHandle()));
					log.info("SQS:{} was deleted", message.getMessageId());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					log.warn("Job for SQS:{} was interrupted", message.getMessageId());
				} catch (ExecutionException e) { // handle e.getCause()
					log.error("Job for SQS:{} was failed", message.getMessageId(), e.getCause());
				} catch (TimeoutException e) { // we need more time
					extendTimeout(message);
					throw e;
				}
				return null;
			});
		} catch (Exception e) { // NOPMD - cc
			log.error("Retry attempt exceeded?", e);
		}
		log.debug("Visibility timeout follow-up task for {} was finished", message.getMessageId());
	}
	
	private void extendTimeout(Message message) {
		log.debug("Job for SQS:{} was timeout RHD:{}", message.getMessageId(), computeReceptHandleDigest(message));
		sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest(
				workerQueueUrl, message.getReceiptHandle(), visibilityTimeout));
		if (log.isDebugEnabled()) {
			log.debug("Visibility for SQS:{} was updated VT:{}", message.getMessageId(), visibilityTimeout);
		} else if (log.isTraceEnabled()) {
			log.trace("Visibility for SQS:{} was updated VT:{} RHD:{}",
					message.getMessageId(),
					visibilityTimeout,
					computeReceptHandleDigest(message));
		}
	}
	
	private Object computeReceptHandleDigest(Message message) {
		return new Object() {
			
			@Override
			public String toString() {
				return DigestUtils.md5DigestAsHex(message.getReceiptHandle().getBytes(StandardCharsets.UTF_8));
			}
		};
	}
}
