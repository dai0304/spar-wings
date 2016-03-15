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
package jp.xet.sparwings.jackson.patch;

/**
 * TODO for daisuke
 */
@SuppressWarnings("serial")
public class IllegalPatchException extends RuntimeException {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param cause
	 */
	public IllegalPatchException(Throwable cause) {
		super(cause);
	}
}
