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
package jp.xet.sparwings.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for {@link Exception}s and {@link Throwable}s.
 * 
 * @since #version#
 * @author daisuke
 */
public final class ExceptionUtil {
	
	/**
	 * Returns stacktrace as string.
	 *
	 * @param t the exception
	 * @return stacktrace
	 * @since 0.1
	 */
	public static String toString(Throwable t) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		
		t.printStackTrace(pw);
		
		return writer.toString();
	}
	
	private ExceptionUtil() {
	}
}
