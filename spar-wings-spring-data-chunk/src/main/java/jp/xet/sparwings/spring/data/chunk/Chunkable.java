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
package jp.xet.sparwings.spring.data.chunk;

import org.springframework.data.domain.Sort.Direction;

/**
 * Abstract interface for value-based pagination information.
 * 
 * @since 0.11
 * @author daisuke
 */
public interface Chunkable {
	
	/**
	 * Returns token for pagination.
	 * 
	 * @return the token
	 * @since 0.24
	 */
	String getPaginationToken();
	
	/**
	 * Returns the relation of current chunk to retrieve.
	 * 
	 * @return the relation
	 * @since 0.24
	 */
	PaginationRelation getPaginationRelation();
	
	/**
	 * Returns the number of items to be returned.
	 * 
	 * @return the number of items of that chunk
	 * @since 0.11
	 */
	Integer getMaxPageSize();
	
	/**
	 * Returns the direction the items sorted.
	 * 
	 * @return the directio
	 * @since 0.11
	 */
	Direction getDirection();
	
	
	enum PaginationRelation {
		NEXT,
		PREV
	}
}
