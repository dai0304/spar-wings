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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * TODO for daisuke
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChunkRequest implements Chunkable {
	
	@Getter
	private Object exclusiveStartKey;
	
	@Getter
	private Integer maxPageSize;
	
	@Getter
	private Direction direction;
	
	
	public ChunkRequest(String exclusiveStartKey) {
		this(exclusiveStartKey, null, null);
	}
	
	public ChunkRequest(Integer maxPageSize) {
		this(null, maxPageSize, null);
	}
	
	public ChunkRequest(String exclusiveStartKey, Integer maxPageSize) {
		this(exclusiveStartKey, maxPageSize, null);
	}
	
	public ChunkRequest(Direction direction) {
		this(null, null, direction);
	}
	
	public ChunkRequest(String exclusiveStartKey, Direction direction) {
		this(exclusiveStartKey, null, direction);
	}
	
	public ChunkRequest(Integer maxPageSize, Direction direction) {
		this(null, maxPageSize, direction);
	}
	
	@Override
	public Chunkable next(Object lastEvaluatedKey) {
		return new ChunkRequest(lastEvaluatedKey, maxPageSize, direction);
	}
}
