/*
 * Copyright 2015 Miyamoto Daisuke.
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
package jp.xet.sparwings.spring.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * SUによる認証トークン。
 * 
 * @since 0.6
 * @version $Id$
 * @author daisuke
 */
@SuppressWarnings("serial")
public class SuperUserAuthenticationToken extends AbstractAuthenticationToken {
	
	private final Object principal;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param principal ユーザ
	 * @param authorities 権限
	 */
	public SuperUserAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}
	
	@Override
	public Object getCredentials() {
		return null;
	}
	
	@Override
	public Object getPrincipal() {
		return principal;
	}
}
