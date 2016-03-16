/*
 * Copyright 2015-2016 Miyamoto Daisuke.
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.domain.Sort.Direction;

/**
 * TODO for daisuke
 * 
 * @since 0.11
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
