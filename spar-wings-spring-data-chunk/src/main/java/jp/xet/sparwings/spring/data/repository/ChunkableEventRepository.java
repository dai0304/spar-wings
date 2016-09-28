/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/09/27
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.xet.sparwings.spring.data.repository;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import org.springframework.dao.DataAccessException;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import jp.xet.sparwings.spring.data.chunk.Chunkable;

/**
 * TODO for daisuke
 * 
 * @param <E> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * @param <EV> the domain event type the repository accepts
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public interface ChunkableEventRepository<E, ID extends Serializable, EV extends UnaryOperator<E> & Serializable>
		extends EventRepository<E, ID, EV> {
	
	/**
	 * TODO for daisuke
	 * 
	 * @param id must not be {@literal null}.
	 * @param chunkable chunking information
	 * @return a chunk of events
	 * @throws DataAccessException データアクセスエラーが発生した場合
	 * @throws NullPointerException 引数に{@code null}を与えた場合
	 * @since #version#
	 */
	Chunk<EV> findEvents(ID id, Chunkable chunkable);
}
