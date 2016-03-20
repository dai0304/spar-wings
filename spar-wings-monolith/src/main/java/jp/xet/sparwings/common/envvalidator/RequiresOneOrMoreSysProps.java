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

/**
 * どれか1つ以上必須。
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
public class RequiresOneOrMoreSysProps extends AbstractRequiresOneOrMoreRequirement {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param keys property keys
	 */
	public RequiresOneOrMoreSysProps(String... keys) {
		super(Arrays.asList(keys));
	}
	
	@Override
	protected boolean exists(String key) {
		return System.getProperty(key) != null;
	}
	
	@Override
	protected String getTargetName() {
		return "System property";
	}
}
