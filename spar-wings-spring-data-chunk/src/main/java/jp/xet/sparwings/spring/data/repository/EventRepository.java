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
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.NoRepositoryBean;

import lombok.RequiredArgsConstructor;

/**
 * Repository interface to accept domain events.
 * 
 * @param <E> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * @param <EV> the domain event type the repository accepts
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@NoRepositoryBean
public interface EventRepository<E, ID extends Serializable, EV extends UnaryOperator<E> & Serializable>
		extends BaseRepository<E, ID> {
	
	/**
	 * Retrieves an entity by its id.
	 * 
	 * @param id must not be {@literal null}.
	 * @param event must not be {@literal null}.
	 * @return the entity with the given event or {@literal null} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}
	 * @throws DataAccessException データアクセスエラーが発生した場合
	 * @since #version#
	 */
	E accept(ID id, EV event);
	
	/**
	 * Interface to be implemented by any domain events that wishes to be notified of the ID that it runs in.
	 * 
	 * @since #version#
	 * @version $Id$
	 * @author daisuke
	 */
	interface IdAware {
		
		/**
		 * TODO for daisuke
		 * 
		 * @param id must not be {@literal null}.
		 * @since #version#
		 */
		void setId(Serializable id);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param <E>
	 * @since #version#
	 * @version $Id$
	 * @author daisuke
	 */
	@RequiredArgsConstructor
	static class ModelCollector<E> implements Collector<UnaryOperator<E>, Object[], E> {
		
		final Serializable id;
		
		final E initialImage;
		
		@Override
		public Supplier<Object[]> supplier() {
			return () -> new Object[] {
				initialImage
			};
		}
		
		@Override
		public BiConsumer<Object[], UnaryOperator<E>> accumulator() {
			return this::accumulator;
		}
		
		@Override
		public BinaryOperator<Object[]> combiner() {
			return null; // ModelCollector is not support combining
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Function<Object[], E> finisher() {
			return ma -> (E) ma[0];
		}
		
		@Override
		public Set<java.util.stream.Collector.Characteristics> characteristics() {
			return Collections.emptySet();
		}
		
		@SuppressWarnings("unchecked")
		private void accumulator(Object[] ma, UnaryOperator<E> e) {
			if (e instanceof IdAware) {
				((IdAware) e).setId(id);
			}
			ma[0] = e.apply((E) ma[0]);
		}
	}
}
