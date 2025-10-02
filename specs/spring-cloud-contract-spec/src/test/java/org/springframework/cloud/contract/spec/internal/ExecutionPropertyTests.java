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

class ExecutionPropertyTests {

	// BDD: should insert passed value in place of $it placeholder
	@Test
	void shouldInsertPassedValueInPlaceOfItPlaceholder() {
		// given
		String commandToExecute = "commandToExecute($it)";
		ExecutionProperty executionProperty = new ExecutionProperty(commandToExecute);
		String valueToInsert = "someObject.itsValue";

		// when
		String commandWithInsertedValue = executionProperty.insertValue(valueToInsert);

		// then
		then(commandWithInsertedValue).isEqualTo("commandToExecute(someObject.itsValue)");
	}

	// BDD: should insert passed value with a $ sign in place of $it placeholder
	@Test
	void shouldInsertPassedValueWithDollarSignInPlaceOfItPlaceholder() {
		// given
		String commandToExecute = "commandToExecute($it)";
		ExecutionProperty executionProperty = new ExecutionProperty(commandToExecute);
		String valueToInsert = "$.someObject.itsValue";

		// when
		String commandWithInsertedValue = executionProperty.insertValue(valueToInsert);

		// then
		then(commandWithInsertedValue).isEqualTo("commandToExecute($.someObject.itsValue)");
	}

}
