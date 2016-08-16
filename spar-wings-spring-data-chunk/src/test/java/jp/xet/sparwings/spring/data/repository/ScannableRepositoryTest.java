/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2016/08/16
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

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Test for {@link ScannableRepository}.
 * 
 * @since 0.26
 * @version $Id$
 * @author daisuke
 */
public class ScannableRepositoryTest {
	
	@SuppressWarnings("unused")
	private static interface AssertTypeCompatibleWithPagingAndSortingRepository
			extends PagingAndSortingRepository<String, Long>, ScannableRepository<String, Long> {
	}
}
