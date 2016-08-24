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
package jp.xet.sparwings.spring.data.repository;

import java.io.Serializable;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Repository interface to delete single entity.
 * 
 * @param <E> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * @param <C> the condition type to update
 * @since 0.27
 * @author daisuke
 */
@NoRepositoryBean
public interface ConditionalDeletableRepository<E, ID extends Serializable, C> extends DeletableRepository<E, ID> {
	
	/**
	 * Deletes a given entity.
	 * 
	 * @param entity entity to delete
	 * @param condition condition
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 * @throws OptimisticLockingFailureException
	 * @throws DataAccessException データアクセスエラーが発生した場合
	 * @since 0.27
	 */
	void delete(E entity, C condition);
	
	/**
	 * Deletes the entity with the given id.
	 * 
	 * @param id must not be {@literal null}.
	 * @param condition condition
	 * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
	 * @throws OptimisticLockingFailureException
	 * @throws DataAccessException データアクセスエラーが発生した場合
	 * @since 0.27
	 */
	void delete(ID id, C condition);
}
