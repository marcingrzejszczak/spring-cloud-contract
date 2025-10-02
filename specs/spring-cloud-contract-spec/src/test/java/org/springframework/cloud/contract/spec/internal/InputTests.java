/*
 * Copyright 2013-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.contract.spec.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author Tim Ysewyn
 */
class InputTests {

	// BDD: should set property when using the $() convenience method
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethod() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{5}")));
		Object generatedValue = property.getServerValue();

		// then
		then(generatedValue).isInstanceOf(String.class);
		int value = Integer.parseInt((String) generatedValue);
		then(value).isBetween(0, 99_999);
	}

	// BDD: should set property when using the $() convenience method for Double
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethodForDouble() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{5}").asDouble()));
		Object value = property.getServerValue();

		// then
		then(value).isInstanceOf(Double.class);
		then((Double) value).isBetween(0.0, 99_999.0);
	}

	// BDD: should set property when using the $() convenience method for Short
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethodForShort() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{1}").asShort()));
		Object value = property.getServerValue();

		// then
		then(value).isInstanceOf(Short.class);
		then((Short) value).isBetween((short) 0, (short) 9);
	}

	// BDD: should set property when using the $() convenience method for Long
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethodForLong() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{5}").asLong()));
		Object value = property.getServerValue();

		// then
		then(value).isInstanceOf(Long.class);
		then((Long) value).isBetween(0L, 99_999L);
	}

	// BDD: should set property when using the $() convenience method for Integer
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethodForInteger() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{5}").asInteger()));
		Object value = property.getServerValue();

		// then
		then(value).isInstanceOf(Integer.class);
		then((Integer) value).isBetween(0, 99_999);
	}

	// BDD: should set property when using the $() convenience method for Float
	@Test
	void shouldSetPropertyWhenUsingDollarConvenienceMethodForFloat() {
		// given
		Input input = new Input();

		// when
		DslProperty<Object> property = input.$(input.consumer(input.regex("[0-9]{5}").asFloat()));
		Object value = property.getServerValue();

		// then
		then(value).isInstanceOf(Float.class);
		then((Float) value).isBetween(0.0f, 99_999.0f);
	}

}
