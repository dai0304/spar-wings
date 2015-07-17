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
package jp.xet.sparwings.aws.dynamodb;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttributeValues {
	
	public static AttributeValue attributeS(String s) {
		return new AttributeValue().withS(s);
	}
	
	public static AttributeValue attributeN(Number n) {
		return new AttributeValue().withN(String.valueOf(n));
	}
	
	public static AttributeValue attributeB(ByteBuffer b) {
		return new AttributeValue().withB(b);
	}
	
	public static AttributeValue attributeSS(String... ss) {
		return new AttributeValue().withSS(ss);
	}
	
	public static AttributeValue attributeNS(Number... ns) {
		return new AttributeValue().withNS(Arrays.asList(ns).stream()
			.map(Object::toString)
			.toArray(String[]::new));
	}
	
	public static AttributeValue attributeBS(ByteBuffer... bs) {
		return new AttributeValue().withBS(bs);
	}
	
	public static AttributeValue attributeM(Map<String, AttributeValue> m) {
		return new AttributeValue().withM(m);
	}
	
	public static AttributeValue attributeL(AttributeValue l) {
		return new AttributeValue().withL(l);
	}
	
	public static AttributeValue attributeNULL(boolean nul) {
		return new AttributeValue().withNULL(nul);
	}
	
	public static AttributeValue attributeBOOL(boolean b) {
		return new AttributeValue().withBOOL(b);
	}
}
