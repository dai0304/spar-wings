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
package jp.xet.sparwings.spring.security;

import java.util.Date;
import java.util.stream.StreamSupport;

import lombok.Setter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.util.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.Assert;

/**
 * Amazon DynamoDB based persistent login token repository implementation.
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
public class DynamoPersistentTokenRepository implements PersistentTokenRepository, InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(DynamoPersistentTokenRepository.class);
	
	private static final String USERNAME = "username";
	
	private static final String SERIES = "series";
	
	private static final String TOKEN = "token";
	
	private static final String LAST_USED = "lastUsed";
	
	@Setter
	private AmazonDynamoDB amazonDynamoDb;
	
	@Setter
	private String persistentLoginTableName;
	
	private DynamoDB dynamoDB;
	
	private Table persistentLoginTable;
	
	
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(amazonDynamoDb, "amazonDynamoDb required");
		Assert.hasText(persistentLoginTableName, "persistentLoginTableName required");
		dynamoDB = new DynamoDB(amazonDynamoDb);
		persistentLoginTable = new Table(amazonDynamoDb, persistentLoginTableName);
	}
	
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		Assert.notNull(token);
		if (logger.isTraceEnabled()) {
			logger.trace("Create token: username={}, series={}, tokenValue={}, date={}", new Object[] {
				token.getUsername(),
				token.getSeries(),
				token.getTokenValue(),
				token.getDate()
			});
		}
		
		try {
			PutItemOutcome putItemOutcome = persistentLoginTable.putItem(new Item()
				.withString(USERNAME, token.getUsername())
				.withString(SERIES, token.getSeries())
				.withString(TOKEN, token.getTokenValue())
				.withString(LAST_USED, DateUtils.formatISO8601Date(token.getDate())));
			if (logger.isDebugEnabled()) {
				logger.debug("Token created: {}", putItemOutcome.getPutItemResult());
			}
		} catch (Exception e) {
			logger.error("unexpected", e);
		}
	}
	
	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		if (logger.isTraceEnabled()) {
			logger.trace("Retrieve token: seriesId={} from table={}", seriesId, persistentLoginTableName);
		}
		
		try {
			Item item = persistentLoginTable.getItem(SERIES, seriesId);
			if (logger.isDebugEnabled()) {
				logger.debug("Token retrieved: {}", item);
			}
			
			if (item == null) {
				if (logger.isInfoEnabled()) {
					logger.info("Querying token for series '{}' returned no results.", seriesId);
				}
				return null;
			}
			
			String username = item.getString(USERNAME);
			String series = item.getString(SERIES);
			String tokenValue = item.getString(TOKEN);
			Date lastUsed = DateUtils.parseISO8601Date(item.getString(LAST_USED));
			return new PersistentRememberMeToken(username, series, tokenValue, lastUsed);
		} catch (AmazonServiceException e) {
			logger.error("Failed to load token for series {}", seriesId, e);
		} catch (AmazonClientException e) {
			logger.error("Failed to load token for series {}", seriesId, e);
		} catch (Exception e) {
			logger.error("unexpected", e);
		}
		return null;
	}
	
	@Override
	public void removeUserTokens(String username) {
		if (logger.isTraceEnabled()) {
			logger.trace("Remove token: username={} from table={}", username, persistentLoginTableName);
		}
		
		try {
			ItemCollection<QueryOutcome> items =
					persistentLoginTable.getIndex("idx_username").query(USERNAME, username);
			Object[] serieses = StreamSupport.stream(items.spliterator(), false)
				.map(item -> item.getString(SERIES))
				.toArray(size -> new Object[size]);
			
			if (serieses.length > 0) {
				BatchWriteItemOutcome batchWriteItemOutcome = dynamoDB
					.batchWriteItem(new TableWriteItems(persistentLoginTableName)
						.withHashOnlyKeysToDelete(SERIES, serieses));
				if (logger.isDebugEnabled()) {
					logger.debug("Token removed: {}", batchWriteItemOutcome.getBatchWriteItemResult());
				}
			}
		} catch (Exception e) {
			logger.error("unexpected", e);
		}
	}
	
	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		if (logger.isTraceEnabled()) {
			logger.trace("Update token: series={}, tokenValue={}, lastUsed={}", new Object[] {
				series,
				tokenValue,
				lastUsed
			});
		}
		
		try {
			String now = DateUtils.formatISO8601Date(new Date());
			
			UpdateItemOutcome updateItemOutcome = persistentLoginTable.updateItem(SERIES, series,
					new AttributeUpdate(TOKEN).put(tokenValue),
					new AttributeUpdate(LAST_USED).put(now));
			if (logger.isDebugEnabled()) {
				logger.debug("Token updated: {}", updateItemOutcome.getUpdateItemResult());
			}
		} catch (Exception e) {
			logger.error("unexpected", e);
		}
	}
}
