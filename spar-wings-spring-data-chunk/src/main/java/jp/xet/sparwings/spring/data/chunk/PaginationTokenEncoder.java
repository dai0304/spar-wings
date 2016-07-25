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

import java.util.Optional;

/**
 * TODO for daisuke
 * 
 * @since 0.24
 * @version $Id$
 * @author daisuke
 */
public interface PaginationTokenEncoder {
	
	/**
	 * TODO for daisuke
	 * 
	 * @param paginationToken token
	 * @return
	 * @since 0.24
	 */
	Optional<String> extractFirstKey(String paginationToken);
	
	/**
	 * TODO for daisuke
	 * 
	 * @param paginationToken token
	 * @return
	 * @since 0.24
	 */
	Optional<String> extractLastKey(String paginationToken);
	
	/**
	 * TODO for daisuke
	 * 
	 * @param firstKey
	 * @param lastKey
	 * @return token
	 * @throws InvalidKeyExpressionException
	 * @since 0.24
	 */
	String encode(Object firstKey, Object lastKey);
	
}
