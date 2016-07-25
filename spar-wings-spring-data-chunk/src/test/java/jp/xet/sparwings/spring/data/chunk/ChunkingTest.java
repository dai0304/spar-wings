/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/07/25
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
package jp.xet.sparwings.spring.data.chunk;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.data.domain.Sort.Direction;

import jp.xet.sparwings.spring.data.chunk.Chunkable.PaginationRelation;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class ChunkingTest {
	
	SampleRepository repo = new SampleRepository();
	
	
	@Test
	public void testASC() {
		Chunkable request = new ChunkRequest(10, Direction.ASC);
		assertThat(request.getPaginationToken(), is(nullValue()));
		assertThat(request.getPaginationRelation(), is(nullValue()));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.ASC));
		
		Chunk<String> chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[aaa, bbb, ccc, ddd, eee, fff, ggg, hhh, iii, jjj]"));
		assertThat(chunk.isFirst(), is(true));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(false));
		assertThat(chunk.prevChunkable(), is(nullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
		
		request = chunk.nextChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.ASC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[kkk, lll, mmm, nnn, ooo, ppp, qqq, rrr, sss, ttt]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
		
		request = chunk.nextChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.ASC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[uuu, vvv, www, xxx, yyy, zzz]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(true));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(false));
		assertThat(chunk.nextChunkable(), is(nullValue()));
		
		// exercise
		request = chunk.prevChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.PREV));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.ASC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[kkk, lll, mmm, nnn, ooo, ppp, qqq, rrr, sss, ttt]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
	}
	
	@Test
	public void testDESC() {
		Chunkable request = new ChunkRequest(10, Direction.DESC);
		assertThat(request.getPaginationToken(), is(nullValue()));
		assertThat(request.getPaginationRelation(), is(nullValue()));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.DESC));
		
		Chunk<String> chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[zzz, yyy, xxx, www, vvv, uuu, ttt, sss, rrr, qqq]"));
		assertThat(chunk.isFirst(), is(true));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(false));
		assertThat(chunk.prevChunkable(), is(nullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
		
		request = chunk.nextChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.DESC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[ppp, ooo, nnn, mmm, lll, kkk, jjj, iii, hhh, ggg]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
		
		request = chunk.nextChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.NEXT));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.DESC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[fff, eee, ddd, ccc, bbb, aaa]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(true));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(false));
		assertThat(chunk.nextChunkable(), is(nullValue()));
		
		// exercise
		request = chunk.prevChunkable();
		assertThat(request.getPaginationToken(), is(chunk.getPaginationToken()));
		assertThat(request.getPaginationRelation(), is(PaginationRelation.PREV));
		assertThat(request.getMaxPageSize(), is(10));
		assertThat(request.getDirection(), is(Direction.DESC));
		
		chunk = repo.findAll(request);
		assertThat(chunk.getContent().toString(), is("[ppp, ooo, nnn, mmm, lll, kkk, jjj, iii, hhh, ggg]"));
		assertThat(chunk.isFirst(), is(false));
		assertThat(chunk.isLast(), is(false));
		assertThat(chunk.hasPrev(), is(true));
		assertThat(chunk.prevChunkable(), is(notNullValue()));
		assertThat(chunk.hasNext(), is(true));
		assertThat(chunk.nextChunkable(), is(notNullValue()));
		
	}
	
	
	private static class SampleRepository {
		
		private PaginationTokenEncoder encoder = new SimplePaginationTokenEncoder();
		
		static final List<String> data = IntStream.rangeClosed('a', 'z')
			.mapToObj(i -> (char) i)
			.map(String::valueOf)
			.map(s -> s + s + s)
			.collect(Collectors.toList());
		
		
		public Chunk<String> findAll(Chunkable chunkable) {
			List<String> source = data;
			
			Direction direction = chunkable.getDirection();
			PaginationRelation relation = chunkable.getPaginationRelation();
			if ((direction == Direction.ASC && relation == PaginationRelation.PREV) ||
					(direction != Direction.ASC && relation != PaginationRelation.PREV)) {
				source = new ArrayList<>(data); // copy
				Collections.reverse(source);
			}
			
			Integer size = Optional.ofNullable(chunkable.getMaxPageSize()).orElse(20);
			List<String> content;
			if (chunkable.getPaginationToken() == null) {
				content = source.stream().limit(size).collect(Collectors.toList());
			} else {
				String key;
				if (relation == PaginationRelation.NEXT) {
					key = encoder.extractLastKey(chunkable.getPaginationToken()).get();
				} else if (relation == PaginationRelation.PREV) {
					key = encoder.extractFirstKey(chunkable.getPaginationToken()).get();
				} else {
					throw new Error();
				}
				content = source.stream()
					.filter(keyFilter(key, relation, direction))
					.limit(size)
					.collect(Collectors.toList());
			}
			
			if (relation == PaginationRelation.PREV) {
				Collections.reverse(content);
			}
			
			String firstKey = (chunkable.getPaginationToken() == null || content.isEmpty()) ? null : content.get(0);
			String lastKey = content.isEmpty() ? null : content.get(content.size() - 1);
			String pt = encoder.encode(firstKey, lastKey);
			return new ChunkImpl<>(content, pt, chunkable);
		}
		
		private Predicate<? super String> keyFilter(String key, PaginationRelation relation, Direction direction) {
			if ((direction == Direction.ASC && relation == PaginationRelation.NEXT) ||
					(direction != Direction.ASC && relation != PaginationRelation.NEXT)) {
				return e -> e.compareTo(key) > 0;
			} else {
				return e -> e.compareTo(key) < 0;
			}
		}
	}
}
