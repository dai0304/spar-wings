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
package jp.xet.sparwings.spring.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Empty implementation of {@link Page}
 * 
 *  * @param <T> the type of which the page consists.
 * @since 0.5
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("serial")
public final class EmptyPage<T> implements Page<T>, Serializable {
	
	private final Pageable pageable;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param pageable the paging information, can be {@literal null}.
	 */
	public EmptyPage(Pageable pageable) {
		this.pageable = pageable;
	}
	
	@Override
	public List<T> getContent() {
		return Collections.emptyList();
	}
	
	@Override
	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}
	
	@Override
	public int getNumberOfElements() {
		return 0;
	}
	
	@Override
	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}
	
	@Override
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}
	
	@Override
	public long getTotalElements() {
		return 0L;
	}
	
	@Override
	public int getTotalPages() {
		return 0;
	}
	
	@Override
	public boolean hasContent() {
		return false;
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}
	
	@Override
	public boolean hasPrevious() {
		return false;
	}
	
	@Override
	public boolean isFirst() {
		return true;
	}
	
	@Override
	public boolean isLast() {
		return true;
	}
	
	@Override
	public Iterator<T> iterator() {
		return Collections.emptyIterator();
	}
	
	@Override
	public Pageable nextPageable() {
		return null;
	}
	
	@Override
	public Pageable previousPageable() {
		return null;
	}
	
	@Override
	public <U> Page<U> map(Function<? super T, ? extends U> converter) {
		return new EmptyPage<>(pageable);
	}
	
}
