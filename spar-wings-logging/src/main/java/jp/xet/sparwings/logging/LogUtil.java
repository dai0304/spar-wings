/*
 * Copyright 2015-2016 Classmethod, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All source code, documentation and other information
 * contained herein is, and remains the property of Classmethod, Inc.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Classmethod, Inc.
 */
package jp.xet.sparwings.logging;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to logging in Java 8 stream APIs.
 *
 * @since 0.28
 * @author daisuke
 */
@Slf4j
@UtilityClass
public class LogUtil {
	
	/**
	 * Log result object.
	 * 
	 * <p>This method is primary intended to use in Stream chaining as following:
	 * <pre><code>
	 * .map(LogUtil::logResult)
	 * </code></pre>
	 * </p>
	 *
	 * @param result the target object to log
	 * @param <T> type of result object
	 * @return the object passed to
	 * @since 0.28
	 */
	public static <T> T logResult(T result) {
		log.debug("Result: {}", result);
		return result;
	}
	
	/**
	 * Log object before apply modification.
	 * 
	 * <p>This method is primary intended to use in Stream chaining as following:
	 * <pre><code>
	 * .map(LogUtil::logBefore)
	 * .map(func)
	 * .map(LogUtil::logAfter)
	 * </code></pre>
	 * </p>
	 *
	 * @param before the object before apply modification
	 * @param <T> type of target object
	 * @return the object passed to
	 * @see #logAfter(Object)
	 * @since 0.28
	 */
	public static <T> T logBefore(T before) {
		log.debug("Before apply: {}", before);
		return before;
	}
	
	/**
	 * Log object after apply modification.
	 * 
	 * <p>This method is primary intended to use in Stream chaining as following:
	 * <pre><code>
	 * .map(LogUtil::logBefore)
	 * .map(func)
	 * .map(LogUtil::logAfter)
	 * </code>
	 * </p></pre>
	 *
	 * @param before the object after apply modification
	 * @param <T> type of target object
	 * @return the object passed to
	 * @see #logBefore(Object)
	 * @since 0.28
	 */
	public static <T> T logAfter(T before) {
		log.debug("After apply: {}", before);
		return before;
	}
}
