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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.util.Assert;

import jp.xet.sparwings.spring.data.chunk.Chunk;
import jp.xet.sparwings.spring.data.chunk.ChunkImpl;
import jp.xet.sparwings.spring.data.chunk.ChunkRequest;
import jp.xet.sparwings.spring.data.chunk.Chunkable;
import jp.xet.sparwings.spring.data.repository.EventRepository.IdAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Concept of {@link EventRepository}.
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@Slf4j
@SuppressWarnings("javadoc")
public class DomainEventTest {
	
	@Test
	public void testStateSourcingModelRepository() {
		doTest(new StateSourcingModelRepository());
	}
	
	@Test
	public void testEventSourcingModelRepository() {
		doTest(new EventSourcingModelRepository());
	}
	
	private void doTest(ModelRepository repo) {
		log.info("Test of {}", repo);
		
		Model created = repo.accept("foo", new CreateEvent());
		log.info("{} : created", created);
		Model foundAfterCreated = repo.findOne("foo");
		log.info("{} : foundAfterCreated", foundAfterCreated);
		assertThat(foundAfterCreated.getData(), is(0));
		
		Model incremented = repo.accept("foo", new IncrementEvent());
		log.info("{} : incremented", incremented);
		Model foundAfterIncremented = repo.findOne("foo");
		log.info("{} : foundAfterIncremented", foundAfterIncremented);
		assertThat(foundAfterIncremented.getData(), is(1));
		
		Model decremented = repo.accept("foo", new DecrementEvent());
		log.info("{} : decremented", decremented);
		Model foundAfterDecremented = repo.findOne("foo");
		log.info("{} : foundAfterDecremented", foundAfterDecremented);
		assertThat(foundAfterDecremented.getData(), is(0));
		
		Model deleted = repo.accept("foo", new DeleteEvent());
		log.info("{} : deleted", deleted);
		Model foundAfterDeleted = repo.findOne("foo");
		log.info("{} : foundAfterDeleted", foundAfterDeleted);
		assertThat(foundAfterDeleted, is(nullValue()));
		
		try {
			repo.accept("bar", new IncrementEvent());
			fail();
		} catch (IllegalStateException e) {
			// success
		}
		
		repo.accept("bar", new CreateEvent());
		try {
			repo.accept("bar", new CreateEvent());
			fail();
		} catch (IllegalStateException e) {
			// success
		}
	}
	
	@Data
	@AllArgsConstructor
	static class Model {
		
		String id;
		
		int data;
		
	}
	
	static interface ModelRepository
			extends EventRepository<Model, String, DomainEvent>, ReadableRepository<Model, String> {
	}
	
	static interface DomainEvent extends UnaryOperator<Model>, Serializable {
	}
	
	static class StateSourcingModelRepository implements ModelRepository {
		
		private final Map<String, Model> states = new HashMap<>();
		
		@Override
		public Model accept(String id, DomainEvent event) {
			Model latest = findOne(id);
			try {
				Model applied = Stream.of(event).collect(new ModelCollector<>(id, latest));
				states.put(id, applied);
				return applied;
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(e);
			}
		}
		
		@Override
		public boolean exists(String id) {
			return states.containsKey(id);
		}
		
		@Override
		public Model findOne(String id) {
			return states.get(id);
		}
		
		@Override
		public String getId(Model entity) {
			return entity.getId();
		}
	}
	
	static class EventSourcingModelRepository
			implements ModelRepository, ChunkableEventRepository<Model, String, DomainEvent> {
		
		private final Map<String, List<DomainEvent>> events = new HashMap<>();
		
		@Override
		public Model accept(String id, DomainEvent event) {
			Model latest = findOne(id);
			try {
				Model applied = Stream.of(event).collect(new ModelCollector<>(id, latest));
				events.computeIfAbsent(id, k -> new ArrayList<>()).add(event);
				return applied;
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(e);
			}
		}
		
		@Override
		public Chunk<DomainEvent> findEvents(String id, Chunkable chunkable) {
			if (chunkable == null) {
				return new ChunkImpl<>(Collections.emptyList(), null, chunkable);
			}
			List<DomainEvent> content = events.computeIfAbsent(id, k -> new ArrayList<>());
			return new ChunkImpl<>(content, null, chunkable);
		}
		
		@Override
		public boolean exists(String id) {
			return findOne(id) != null;
		}
		
		@Override
		public Model findOne(String id) {
			Model modelImage = null;
			Chunkable chunkable = new ChunkRequest();
			while (chunkable != null) {
				Chunk<DomainEvent> chunk = findEvents(id, chunkable);
				modelImage = chunk.stream().sequential().collect(new ModelCollector<>(id, modelImage));
				chunkable = chunk.nextChunkable();
			}
			return modelImage;
		}
		
		@Override
		public String getId(Model entity) {
			return entity.getId();
		}
	}
	
	@SuppressWarnings("serial")
	static class CreateEvent implements DomainEvent, IdAware {
		
		@Setter
		Serializable id;
		
		@Override
		public Model apply(Model t) {
			Assert.isNull(t);
			return new Model(Objects.toString(id), 0);
		}
	}
	
	@SuppressWarnings("serial")
	static class IncrementEvent implements DomainEvent {
		
		@Override
		public Model apply(Model t) {
			Assert.notNull(t);
			return new Model(t.getId(), t.getData() + 1);
		}
	}
	
	@SuppressWarnings("serial")
	static class DecrementEvent implements DomainEvent {
		
		@Override
		public Model apply(Model t) {
			Assert.notNull(t);
			Assert.isTrue(t.getData() > 0);
			return new Model(t.getId(), t.getData() - 1);
		}
	}
	
	@SuppressWarnings("serial")
	static class DeleteEvent implements DomainEvent {
		
		@Override
		public Model apply(Model t) {
			Assert.notNull(t);
			return null;
		}
	}
}
