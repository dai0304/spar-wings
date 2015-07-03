/*
 * Copyright 2015 Miyamoto Daisuke, Inc.
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
package jp.xet.sparwings.spring.env;

import java.util.Arrays;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * システム環境（spring profile）を判定するサービスクラス。
 * 
 * @since #version#
 * @author daisuke
 */
public class EnvironmentService {
	
	@Autowired
	Environment env;
	
	
	/**
	 * 指定したspring profileが現在アクティブかどうかを返す。
	 * 
	 * @param profileName spring profile名
	 * @return アクティブな場合は{@code true}、そうでない場合は{@code false}
	 * @throws NullPointerException 引数に{@code null}を与えた場合
	 * @since #version#
	 */
	public boolean is(String profileName) {
		Preconditions.checkNotNull(profileName);
		return Arrays.asList(env.getActiveProfiles()).contains(profileName);
	}
	
	/**
	 * 現在アクティブなspring profileをカンマ区切り文字列で返す。
	 * 
	 * @return 現在アクティブなspring profile
	 * @since #version#
	 */
	public String getActiveProfilesAsString() {
		return Joiner.on(',').join(env.getActiveProfiles());
	}
}
