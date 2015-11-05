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

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * どれか1つ以上必須。
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class RequiresOneOrMoreProfiles extends AbstractRequiresOneOrMoreRequirement {
	
	private final ConfigurableEnvironment env;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param env environment object
	 * @param profileNames profile names
	 */
	public RequiresOneOrMoreProfiles(ConfigurableEnvironment env, String... profileNames) {
		super(Arrays.asList(profileNames));
		this.env = env;
	}
	
	@Override
	protected boolean exists(String profileName) {
		return Arrays.asList(env.getActiveProfiles()).contains(profileName);
	}
	
	@Override
	protected String getTargetName() {
		return "Environment profile";
	}
}