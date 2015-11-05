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
package jp.xet.sparwings.common.envvalidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * TODO for daisuke
 * 
 * @since 0.4
 * @version $Id$
 * @author daisuke
 */
@AllArgsConstructor
public class EnvironmentValidator {
	
	/**
	 * TODO for daisuke
	 * 
	 * @param requirements
	 * @return
	 * @since 0.4
	 */
	public static Collection<String> validate(Collection<ApplicationRequirement> requirements) {
		return new EnvironmentValidator(requirements).getViolationMessages();
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @param requirements
	 * @return
	 * @since 0.4
	 */
	public static Collection<String> validate(ApplicationRequirement... requirements) {
		return validate(Arrays.asList(requirements));
	}
	
	
	@NonNull
	private Collection<ApplicationRequirement> requirements;
	
	
	/**
	 * TODO for daisuke
	 * 
	 * @return
	 * @since 0.4
	 */
	public Collection<String> getViolationMessages() {
		return requirements.stream()
			.filter(ApplicationRequirement::violation)
			.map(ApplicationRequirement::getViolationMessage)
			.collect(Collectors.toList());
	}
	
}
