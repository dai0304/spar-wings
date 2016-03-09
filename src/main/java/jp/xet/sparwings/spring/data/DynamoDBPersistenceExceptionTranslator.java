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
package jp.xet.sparwings.spring.data;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.ItemCollectionSizeLimitExceededException;
import com.amazonaws.services.dynamodbv2.model.LimitExceededException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * TODO for daisuke
 */
public class DynamoDBPersistenceExceptionTranslator implements PersistenceExceptionTranslator {
	
	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof AmazonClientException == false) {
			return null;
		}
		
		if (ex instanceof ProvisionedThroughputExceededException) {
			return new TransientDataAccessResourceException(ex.getMessage(), ex);
		}
		
		if (ex instanceof ConditionalCheckFailedException) {
			return new OptimisticLockingFailureException(ex.getMessage(), ex);
		}
		
		if (ex instanceof InternalServerErrorException) {
			return new DynamoDBSystemException(ex.getMessage(), ex);
		}
		
		if (ex instanceof ResourceNotFoundException) {
			return new InvalidDataAccessResourceUsageException(ex.getMessage(), ex);
		}
		
		if (ex instanceof ResourceInUseException || ex instanceof LimitExceededException) {
			return new TransientDataAccessResourceException(ex.getMessage(), ex);
		}
		
		if (ex instanceof ItemCollectionSizeLimitExceededException) {
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		
		return null;
	}
	
	
	@SuppressWarnings("serial")
	private static class DynamoDBSystemException extends UncategorizedDataAccessException {
		
		public DynamoDBSystemException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}
}
