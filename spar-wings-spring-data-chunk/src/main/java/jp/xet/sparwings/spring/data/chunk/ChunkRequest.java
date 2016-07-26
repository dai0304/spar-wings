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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Basic Java Bean implementation of {@code Chunkable}.
 * 
 * @since 0.11
 * @author daisuke
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ChunkRequest implements Chunkable {
	
	@Getter
	private String paginationToken;
	
	@Getter
	private PaginationRelation paginationRelation;
	
	@Getter
	private Integer maxPageSize;
	
	@Getter
	private Direction direction;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param paginationToken the token
	 */
	public ChunkRequest(String paginationToken) {
		this(paginationToken, PaginationRelation.NEXT, null, Direction.ASC);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param maxPageSize max size
	 */
	public ChunkRequest(Integer maxPageSize) {
		this(null, null, maxPageSize, Direction.ASC);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param paginationToken the token
	 * @param maxPageSize max size
	 */
	public ChunkRequest(String paginationToken, Integer maxPageSize) {
		this(paginationToken, PaginationRelation.NEXT, maxPageSize, Direction.ASC);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param direction the sort direction
	 */
	public ChunkRequest(Direction direction) {
		this(null, null, null, direction);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param paginationToken the token
	 * @param direction the sort direction
	 */
	public ChunkRequest(String paginationToken, Direction direction) {
		this(paginationToken, PaginationRelation.NEXT, null, direction);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param maxPageSize max size
	 * @param direction the sort direction
	 */
	public ChunkRequest(Integer maxPageSize, Direction direction) {
		this(null, null, maxPageSize, direction);
	}
}
