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
package jp.xet.sparwings.common.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link JsonSerializer} implementation to serialize {@link BigDecimal} with specified format.
 * 
 * @since 0.3
 * @author daisuke
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
	
	private final DecimalFormat format;
	
	
	/**
	 * Create instance with {@code format}.
	 * 
	 * @param format format
	 * @since 0.1
	 */
	public BigDecimalSerializer(DecimalFormat format) {
		this.format = format;
	}
	
	@Override
	public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		String string = (value == null) ? "" : format.format(value);
		jgen.writeNumber(string);
	}
	
	@Override
	public Class<BigDecimal> handledType() {
		return BigDecimal.class;
	}
}
