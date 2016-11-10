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
package jp.xet.sparwings.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * @author daisuke
 */
public class ByteBufferUtils {
	
	public static ByteBuffer serialize(Object state) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		
		try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(state);
			oos.flush();
			byte[] bytes = bos.toByteArray();
			return ByteBuffer.wrap(bytes);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static <T> T deserialize(ByteBuffer byteBuffer) {
		// AWS Docs suggest that byteBuffer.array() can in fact be used, so using that directly
		ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer.array());
		try (ObjectInputStream oip = new ObjectInputStream(bais)) {
			@SuppressWarnings("unchecked")
			T result = (T) oip.readObject();
			return result; // NOPMD
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
