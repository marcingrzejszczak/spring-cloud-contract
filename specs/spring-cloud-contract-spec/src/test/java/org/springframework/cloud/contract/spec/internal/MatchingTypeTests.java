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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author Marcin Grzejszczak
 */
class MatchingTypeTests {

	// BDD: should return expected value for type
	@ParameterizedTest(name = "[{index}] type={0}, expected={1}")
	@CsvSource({
			"EQUALITY, false",
			"TYPE, false",
			"COMMAND, false",
			"REGEX, true",
			"DATE, true",
			"TIME, true",
			"TIMESTAMP, true"
	})
	void shouldReturnExpectedValueForType(MatchingType type, boolean expected) {
		// when
		boolean result = MatchingType.regexRelated(type);

		// then
		then(result).isEqualTo(expected);
	}

}
