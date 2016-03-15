/*
 * Copyright 2015-2016 Classmethod, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All source code, documentation and other information
 * contained herein is, and remains the property of Classmethod, Inc.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Classmethod, Inc.
 */
package jp.xet.sparwings.spring.data.chunk;

import org.springframework.data.domain.Sort.Direction;

/**
 * TODO for daisuke
 */
public interface Chunkable {
	
	Object getExclusiveStartKey();
	
	Integer getMaxPageSize();
	
	Direction getDirection();
	
	/**
	 * TODO for daisuke
	 * 
	 * @param lastEvaluatedKey
	 * @return
	 */
	Chunkable next(Object lastEvaluatedKey);
	
}
