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

import lombok.RequiredArgsConstructor;

/**
 * TODO for daisuke
 */
@RequiredArgsConstructor
public class Difference {
	
	public final Object original;
	
	public final Object updated;
	
	
	public static Difference of(Object original, Object updated) {
		return new Difference(original, updated);
	}
}
