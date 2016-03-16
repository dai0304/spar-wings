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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.domain.Sort.Direction;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author daisuke
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
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param exclusiveStartKey
	 */
	public ChunkRequest(String exclusiveStartKey) {
		this(exclusiveStartKey, null, null);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param maxPageSize
	 */
	public ChunkRequest(Integer maxPageSize) {
		this(null, maxPageSize, null);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param exclusiveStartKey
	 * @param maxPageSize
	 */
	public ChunkRequest(String exclusiveStartKey, Integer maxPageSize) {
		this(exclusiveStartKey, maxPageSize, null);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param direction
	 */
	public ChunkRequest(Direction direction) {
		this(null, null, direction);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param exclusiveStartKey
	 * @param direction
	 */
	public ChunkRequest(String exclusiveStartKey, Direction direction) {
		this(exclusiveStartKey, null, direction);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param maxPageSize
	 * @param direction
	 */
	public ChunkRequest(Integer maxPageSize, Direction direction) {
		this(null, maxPageSize, direction);
	}
	
	@Override
	public Chunkable next(Object lastEvaluatedKey) {
		return new ChunkRequest(lastEvaluatedKey, maxPageSize, direction);
	}
}
