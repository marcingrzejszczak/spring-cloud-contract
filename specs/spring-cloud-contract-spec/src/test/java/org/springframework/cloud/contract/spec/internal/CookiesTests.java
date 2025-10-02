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

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author Marcin Grzejszczak
 */
class CookiesTests {

	private Cookies cookies;

	@BeforeEach
	void setup() {
		cookies = createCookies();
	}

	// BDD: should convert cookies to a stub side map
	@Test
	void shouldConvertCookiesToStubSideMap() {
		// when
		Map<String, Object> stubSideMap = cookies.asStubSideMap();

		// then
		then(stubSideMap).containsEntry("foo", "client");
		then(stubSideMap).containsEntry("bar", "client");
	}

	// BDD: should convert cookies to a test side map
	@Test
	void shouldConvertCookiesToTestSideMap() {
		// when
		Map<String, Object> testSideMap = cookies.asTestSideMap();

		// then
		then(testSideMap).containsEntry("foo", "server");
		then(testSideMap).containsEntry("bar", "server");
	}

	private Cookies createCookies() {
		Cookies cookies = new Cookies();
		cookies.cookie("foo", new DslProperty<>("client", "server"));
		cookies.cookie(Map.of("bar", new DslProperty<>("client", "server")));
		return cookies;
	}

}
