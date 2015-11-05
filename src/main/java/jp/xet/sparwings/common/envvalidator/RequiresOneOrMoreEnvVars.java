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

/**
 * どれか1つ以上必須。
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
public class RequiresOneOrMoreEnvVars extends AbstractRequiresOneOrMoreRequirement {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param envVars environment variable names
	 */
	public RequiresOneOrMoreEnvVars(String... envVars) {
		super(Arrays.asList(envVars));
	}
	
	@Override
	protected boolean exists(String key) {
		return System.getenv(key) != null;
	}
	
	@Override
	protected String getTargetName() {
		return "Environment variable";
	}
}
