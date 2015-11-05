/*
 * Copyright 2013 Daisuke Miyamoto.
 * Created on 2015/10/27
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
package jp.xet.sparwings.common.envvalidator;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Iterables;

/**
 * いずれか1つ以上の設定値が必要。
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
public abstract class AbstractRequiresOneOrMoreRequirement implements ApplicationRequirement {
	
	private final Collection<String> keys;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param keys keys
	 */
	public AbstractRequiresOneOrMoreRequirement(String... keys) {
		this(Arrays.asList(keys));
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param keys keys
	 */
	public AbstractRequiresOneOrMoreRequirement(Collection<String> keys) {
		this.keys = keys;
	}
	
	@Override
	public boolean violation() {
		return keys.stream().anyMatch(this::exists);
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param key
	 * @return
	 * @since 0.4
	 */
	protected abstract boolean exists(String key);
	
	/**
	 * TODO for daisuke
	 * 
	 * @return
	 * @since 0.4
	 */
	protected abstract String getTargetName();
	
	@Override
	public String getViolationMessage() {
		if (keys.size() == 1) {
			return String.format("{} {} is required.", getTargetName(), Iterables.getOnlyElement(keys));
		} else {
			return String.format("{} {} are required one or more.", getTargetName(), keys);
		}
	}
}
