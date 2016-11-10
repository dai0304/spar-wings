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
package jp.xet.sparwings.common.envvalidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import com.google.common.collect.Iterables;

/**
 * すべての設定値が必要。
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
public abstract class AbstractRequiresAllRequirement implements ApplicationRequirement {
	
	private final Collection<String> keys;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param keys keys
	 */
	public AbstractRequiresAllRequirement(String... keys) {
		this(Arrays.asList(keys));
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param keys keys
	 */
	public AbstractRequiresAllRequirement(Collection<String> keys) {
		this.keys = keys;
	}
	
	@Override
	public boolean violation() {
		return keys.stream().allMatch(this::exists);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param key key
	 * @return if the key exists
	 * @since 0.4
	 */
	protected abstract boolean exists(String key);
	
	/**
	 * TODO for daisuke
	 * 
	 * @return target name
	 * @since 0.4
	 */
	protected abstract String getTargetName();
	
	@Override
	public String getViolationMessage() {
		if (keys.size() == 1) {
			return String.format(Locale.ENGLISH, "{} {} is required.", getTargetName(), Iterables.getOnlyElement(keys));
		} else {
			return String.format(Locale.ENGLISH, "{} {} are all required.", getTargetName(), keys);
		}
	}
}
