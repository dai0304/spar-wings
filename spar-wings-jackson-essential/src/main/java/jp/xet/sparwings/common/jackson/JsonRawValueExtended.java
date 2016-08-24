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
package jp.xet.sparwings.common.jackson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * {@link JsonRawValue} annotation alternative to support deserialize feature.
 * 
 * @author daisuke
 * @since 0.27
 */
@Target({
	ElementType.ANNOTATION_TYPE,
	ElementType.METHOD,
	ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside // http://bit.ly/2bdrv7J
@JsonRawValue
@JsonDeserialize(using = PojoNodeStringDeserializer.class)
public @interface JsonRawValueExtended {
}
