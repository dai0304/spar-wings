/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/20
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

import org.springframework.data.repository.NoRepositoryBean;

/**
 * Repository interface to retrieve multiple entities in batch.
 * 
 * @param <E> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * @since #version#
 * @author daisuke
 */
@NoRepositoryBean
public interface BatchReadableRepository<E, ID extends Serializable>extends ReadableRepository<E, ID> {
	
	/**
	 * Returns all instances of the type with the given IDs.
	 * 
	 * @param ids set of ID
	 * @return set of entities
	 */
	Iterable<E> findAll(Iterable<ID> ids);
	
}