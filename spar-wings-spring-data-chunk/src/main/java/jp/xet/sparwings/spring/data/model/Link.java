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
package jp.xet.sparwings.spring.data.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * TODO for daisuke
 */
@SuppressWarnings("serial")
@XmlType(name = "link", namespace = Link.ATOM_NAMESPACE)
@JsonIgnoreProperties("templated")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class Link implements Serializable {
	
	public static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
	
	public static final String REL_SELF = "self";
	
	public static final String REL_FIRST = "first";
	
	public static final String REL_PREVIOUS = "prev";
	
	public static final String REL_NEXT = "next";
	
	public static final String REL_LAST = "last";
	
	/**
	 * the actual URI the link is pointing to.
	 */
	@XmlAttribute
	@Getter
	private String href;
	
	@XmlAttribute
	@Getter
	private boolean templated;
	
	public Link(String href) {
		this(href, false);
	}
}
