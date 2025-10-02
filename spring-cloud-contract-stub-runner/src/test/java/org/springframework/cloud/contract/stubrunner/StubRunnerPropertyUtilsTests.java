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

package org.springframework.cloud.contract.stubrunner;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.BDDAssertions.then;

class StubRunnerPropertyUtilsTests {

	@ParameterizedTest(name = "[{index}] queriedProp={0}, systemProperty={1}, envVariable={2}, expectedEnvVar={3}, expectedResult={4}")
	@CsvSource({
			"foo.bar-baz, , , FOO_BAR_BAZ, false",
			"foo.bar-baz, , true, FOO_BAR_BAZ, true",
			"foo.bar-baz, , false, FOO_BAR_BAZ, false",
			"foo.bar-baz, false, true, FOO_BAR_BAZ, false",
			"foo.bar-baz, true, true, FOO_BAR_BAZ, true"
	})
	void shouldReturnExpectedResultWhenCheckingIfQueriedPropIsSet(String queriedProp, String systemProperty,
			String envVariable, String expectedEnvVar, boolean expectedResult) {
		String sysProp = systemProperty;
		String envVar = envVariable;
		String expectedEnv = expectedEnvVar;
		PropertyFetcher fetcher = new PropertyFetcher() {
			@Override
			String systemProp(String prop) {
				return sysProp;
			}

			@Override
			String envVar(String prop) {
				then(prop).isIn(expectedEnv,
						"SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_" + expectedEnv);
				return envVar;
			}
		};
		StubRunnerPropertyUtils.FETCHER = fetcher;

		boolean result = StubRunnerPropertyUtils.isPropertySet(queriedProp);

		then(result).isEqualTo(expectedResult);
	}

	@ParameterizedTest(name = "[{index}] queriedProp={0}, mapKey={1}, mapValue={2}, systemProperty={3}, envVariable={4}, expectedResult={5}, assertedSystemProp={6}, assertedEnvVar={7}")
	@CsvSource({
			"foo.bar-baz, foo.bar-baz, faz, ab, bc, faz, spring.cloud.contract.stubrunner.properties.foo.bar-baz, SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_FOO_BAR_BAZ",
			"foo.bar-baz, , , ab, bc, ab, spring.cloud.contract.stubrunner.properties.foo.bar-baz, SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_FOO_BAR_BAZ",
			"foo.bar-baz, , , '', bc, bc, spring.cloud.contract.stubrunner.properties.foo.bar-baz, SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_FOO_BAR_BAZ",
			"foo.bar-baz, , , '', bc, bc, spring.cloud.contract.stubrunner.properties.foo.bar-baz, SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_FOO_BAR_BAZ"
	})
	void shouldReturnExpectedResultWhenQueriedForQueriedProp(String queriedProp, String mapKey, String mapValue,
			String systemProperty, String envVariable, String expectedResult, String assertedSystemProp,
			String assertedEnvVar) {
		Map<String, String> map = new HashMap<>();
		if (mapKey != null && !mapKey.isEmpty()) {
			map.put(mapKey, mapValue);
		}
		String sysProp = systemProperty;
		String envVar = envVariable;
		String checkedSysProp = assertedSystemProp;
		String checkedEnvVar = assertedEnvVar;
		PropertyFetcher fetcher = new PropertyFetcher() {
			@Override
			String systemProp(String prop) {
				then(prop).isIn(checkedSysProp,
						checkedSysProp.replace("spring.cloud.contract.stubrunner.properties.", ""));
				return sysProp;
			}

			@Override
			String envVar(String prop) {
				then(prop).isIn(checkedEnvVar,
						checkedEnvVar.replace("SPRING_CLOUD_CONTRACT_STUBRUNNER_PROPERTIES_", ""));
				return envVar;
			}
		};
		StubRunnerPropertyUtils.FETCHER = fetcher;

		String result = StubRunnerPropertyUtils.getProperty(map, queriedProp);

		then(result).isEqualTo(expectedResult);
	}

	@ParameterizedTest(name = "[{index}] queriedProp={0}, mapKey={1}, mapValue={2}, systemProperty={3}, envVariable={4}, expectedResult={5}")
	@CsvSource({
			"foo.bar-baz, foo.bar-baz, faz, ab, bc, true",
			"foo.bar-baz, , , ab, bc, true",
			"foo.bar-baz, , , '', bc, true",
			"foo.bar-baz, , , '', bc, true",
			"foo.bar-baz, , , , , false"
	})
	void shouldReturnExpectedResultWhenPropIsSet(String queriedProp, String mapKey, String mapValue,
			String systemProperty, String envVariable, boolean expectedResult) {
		Map<String, String> map = new HashMap<>();
		if (mapKey != null && !mapKey.isEmpty()) {
			map.put(mapKey, mapValue);
		}
		if (map.isEmpty() && mapKey != null && mapKey.isEmpty()) {
			map = null;
		}
		String sysProp = systemProperty;
		String envVar = envVariable;
		PropertyFetcher fetcher = new PropertyFetcher() {
			@Override
			String systemProp(String prop) {
				return sysProp;
			}

			@Override
			String envVar(String prop) {
				return envVar;
			}
		};
		StubRunnerPropertyUtils.FETCHER = fetcher;

		boolean result = StubRunnerPropertyUtils.hasProperty(map, queriedProp);

		then(result).isEqualTo(expectedResult);
	}

	@AfterAll
	static void cleanup() {
		StubRunnerPropertyUtils.FETCHER = new PropertyFetcher();
	}

}
