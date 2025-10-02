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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.BDDAssertions.then;

class RegexPatternsTests {

	// BDD: should generate a regex for ip address that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"123.123.123.123, true",
			"a.b., false"
	})
	void shouldGenerateRegexForIpAddress(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.ipAddress().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for hostname that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"https://asd.com, true",
			"https://asd.com:8080, true",
			"https://localhost, true",
			"https://localhost:8080, true",
			"https://asd.com/asd, false",
			"asd.com, false"
	})
	void shouldGenerateRegexForHostname(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.hostname().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for email that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"asd@asd.com, true",
			"a.b., false",
			"asdf@asdf.online, true"
	})
	void shouldGenerateRegexForEmail(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.email().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for url that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource(delimiter = '|', value = {
			"ftp://asd.com:9090/asd/a?a=b | true",
			"http://www.foo.com/blah_blah | true",
			"ftp://localhost/api | true",
			"http://localhost:8080/api | true",
			"http://www.foo.com/blah_blah/ | true",
			"http://www.foo.com/blah_blah_(wikipedia) | true",
			"http://www.foo.com/blah_blah_(wikipedia)_(again) | true",
			"https://www.example.com/wpstyle/?p=364 | true",
			"https://www.example.com/foo/?bar=baz&inga=42&quux | true",
			"http://✪df.ws/123 | true",
			"https://userid:password@example.com:8080 | true",
			"https://userid:password@example.com:8080/ | true",
			"https://userid@example.com | true",
			"https://userid@example.com/ | true",
			"https://userid@example.com:8080 | true",
			"https://userid@example.com:8080/ | true",
			"https://userid:password@example.com | true",
			"https://userid:password@example.com/ | true",
			"https://142.42.1.1/ | true",
			"https://142.42.1.1:8080/ | true",
			"http://⌘.ws | true",
			"http://⌘.ws/ | true",
			"http://www.foo.com/blah_(wikipedia)#cite-1 | true",
			"http://www.foo.com/blah_(wikipedia)_blah#cite-1 | true",
			"http://www.foo.com/unicode_(✪)_in_parens | true",
			"http://www.foo.com/(something)?after=parens | true",
			"http://☺.damowmow.com/ | true",
			"https://code.google.com/events/#&product=browser | true",
			"https://j.mp | true",
			"ftp://foo.bar/baz | true",
			"https://foo.bar/?q=Test%20URL-encoded%20stuff | true",
			"https://1224.net/ | true",
			"https://a.b-c.de | true",
			"https://223.255.255.254 | true",
			"foo.com | true",
			"a.b. | false",
			"http:// | false",
			"http://. | false",
			"http://.. | false",
			"https://../ | false",
			"http://? | false",
			"http://?? | false",
			"https://??/ | false",
			"http://# | false",
			"http://## | false",
			"http://##/ | false",
			"https://foo.bar?q=Spaces should be encoded | false",
			"// | false",
			"//a | false",
			"///a | false",
			"/// | false",
			"https:///a | false",
			"rdar://1234 | false",
			"h://test | false",
			"http:// shouldfail.com | false",
			":// should fail | false",
			"https://foo.bar/foo(bar)baz quux | false",
			"https://-error-.invalid/ | false",
			"https://-a.b.co | false",
			"https://a.b-.co | false",
			"https://1.1.1.1.1 | false",
			"https://123.123.123 | false",
			"https://3628126748 | false",
			"https://.www.foo.bar/ | false",
			"https://www.foo.bar./ | false",
			"https://.www.foo.bar./ | false"
	})
	void shouldGenerateRegexForUrl(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.url().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for httpsUrl that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource(delimiter = '|', value = {
			"ftp://asd.com:9090/asd/a?a=b | false",
			"https://foo.com/blah_blah/ | true",
			"https://foo.com/blah_blah | true",
			"http://www.foo.com/blah_blah | false",
			"http://www.foo.com/blah_blah/ | false",
			"https://foo.com/blah_blah_(wikipedia) | true",
			"https://foo.com/blah_blah_(wikipedia)_(again) | true",
			"https://www.example.com/wpstyle/?p=364 | true",
			"https://www.example.com/foo/?bar=baz&inga=42&quux | true",
			"https://✪df.ws/123 | true",
			"https://userid:password@example.com:8080 | true",
			"https://userid:password@example.com:8080/ | true",
			"https://userid@example.com | true",
			"https://userid@example.com/ | true",
			"https://userid@example.com:8080 | true",
			"https://userid@example.com:8080/ | true",
			"https://userid:password@example.com | true",
			"https://userid:password@example.com/ | true",
			"https://142.42.1.1/ | true",
			"https://142.42.1.1:8080/ | true",
			"https://⌘.ws | true",
			"https://⌘.ws/ | true",
			"https://foo.com/blah_(wikipedia)#cite-1 | true",
			"https://foo.com/blah_(wikipedia)_blah#cite-1 | true",
			"https://foo.com/unicode_(✪)_in_parens | true",
			"https://foo.com/(something)?after=parens | true",
			"https://☺.damowmow.com/ | true",
			"https://code.google.com/events/#&product=browser | true",
			"https://j.mp | true",
			"ftp://foo.bar/baz | false",
			"https://foo.bar/?q=Test%20URL-encoded%20stuff | true",
			"https://1337.net | true",
			"https://a.b-c.de | true",
			"https://223.255.255.254 | true",
			"foo.com | false",
			"a.b. | false",
			"https:// | false",
			"https://. | false",
			"https://.. | false",
			"https://../ | false",
			"https://? | false",
			"https://?? | false",
			"https://??/ | false",
			"https://# | false",
			"https://## | false",
			"https://##/ | false",
			"https://foo.bar?q=Spaces should be encoded | false",
			"// | false",
			"//a | false",
			"///a | false",
			"/// | false",
			"https:///a | false",
			"rdar://1234 | false",
			"h://test | false",
			"https:// shouldfail.com | false",
			":// should fail | false",
			"https://foo.bar/foo(bar)baz quux | false",
			"https://-error-.invalid/ | false",
			"https://-a.b.co | false",
			"https://a.b-.co | false",
			"https://1.1.1.1.1 | false",
			"https://123.123.123 | false",
			"https://3628126748 | false",
			"https://.www.foo.bar/ | false",
			"https://www.foo.bar./ | false",
			"https://.www.foo.bar./ | false"
	})
	void shouldGenerateRegexForHttpsUrl(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.httpsUrl().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for number that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"1, true",
			"1.0, true",
			"0.1, true",
			".1, true",
			"1., false"
	})
	void shouldGenerateRegexForNumber(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.number().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for positive integer that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"1, true",
			"12345, true",
			"-1, false",
			"0, false",
			"1.0, false"
	})
	void shouldGenerateRegexForPositiveInt(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.positiveInt().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for double that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"1, false",
			"1.0, true",
			"0.1, true",
			".1, true",
			"1., false"
	})
	void shouldGenerateRegexForDouble(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.aDouble().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for uuid that matches correctly
	@Test
	void shouldGenerateRegexForUuid() {
		// given
		String validUuid1 = java.util.UUID.randomUUID().toString();
		String validUuid2 = java.util.UUID.randomUUID().toString().toUpperCase();
		String invalidUuid1 = java.util.UUID.randomUUID().toString() + "!";
		String invalidUuid2 = "23e4567-z89b-12z3-j456-426655440000";
		String invalidUuid3 = "dog";
		String invalidUuid4 = "5";

		// when / then
		then(RegexPatterns.uuid().matcher(validUuid1).matches()).isTrue();
		then(RegexPatterns.uuid().matcher(validUuid2).matches()).isTrue();
		then(RegexPatterns.uuid().matcher(invalidUuid1).matches()).isFalse();
		then(RegexPatterns.uuid().matcher(invalidUuid2).matches()).isFalse();
		then(RegexPatterns.uuid().matcher(invalidUuid3).matches()).isFalse();
		then(RegexPatterns.uuid().matcher(invalidUuid4).matches()).isFalse();
	}

	// BDD: should generate a regex for uuid v4 that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"123e4567-e89b-42d3-a456-556642440000, true",
			"00000000-0000-4000-8000-000000000000, true",
			"00000000-0000-4000-9000-000000000000, true",
			"00000000-0000-4000-a000-000000000000, true",
			"00000000-0000-4000-b000-000000000000, true",
			"00000000-0000-4000-1000-000000000000, false",
			"00000000-0000-0000-0000-000000000000, false",
			"dog, false",
			"5, false"
	})
	void shouldGenerateRegexForUuid4(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.uuid4().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for iso date that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"2014-03-01, true",
			"1014-03-01, true",
			"1014-3-01, false",
			"14-03-01, false",
			"1014-12-01, true",
			"1014-12-31, true",
			"1014-12-1, false",
			"1014-12-32, false",
			"1014-13-31, false",
			"1014-20-30, false",
			"5, false"
	})
	void shouldGenerateRegexForIsoDate(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.isoDate().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for iso datetime that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"2014-03-01T12:23:45, true",
			"1014-03-01T23:59:59, true",
			"1014-3-01T01:01:01, false",
			"1014-03-01T00:00:00, true",
			"1014-03-01T00:00:0, false",
			"1014-03-01T00:0:01, false",
			"1014-03-01T0:01:01, false",
			"1014-03-0100:01:01, false",
			"14-03-01T12:23:45, false",
			"1014-12-01T12:23:45, true",
			"1014-12-31T12:23:45, true",
			"1014-12-1T12:23:45, false",
			"1014-12-32T12:23:45, false",
			"1014-13-31T12:23:45, false",
			"1014-20-30T12:23:45, false",
			"1014-20-30T24:23:45, false",
			"1014-20-30T23:60:45, false",
			"1014-20-30T23:59:60, false"
	})
	void shouldGenerateRegexForIsoDateTime(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.isoDateTime().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for iso time that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"12:23:45, true",
			"23:59:59, true",
			"00:00:00, true",
			"00:00:0, false",
			"00:0:01, false",
			"0:01:01, false",
			"24:23:45, false",
			"23:60:45, false",
			"23:59:60, false"
	})
	void shouldGenerateRegexForIsoTime(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.isoTime().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for iso8601 with offset that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"2014-03-01T12:23:45Z, true",
			"2014-03-01T12:23:45+01:00, true",
			"2014-03-01T12:23:45.1Z, true",
			"2014-03-01T12:23:45.12Z, true",
			"2014-03-01T12:23:45.123Z, true",
			"2014-03-01T12:23:45.1234Z, true",
			"2014-03-01T12:23:45.12345Z, true",
			"2014-03-01T12:23:45.123456Z, true",
			"2014-03-01T12:23:45.1+01:00, true",
			"2014-03-01T12:23:45.12+01:00, true",
			"2014-03-01T12:23:45.123+01:00, true",
			"2014-03-01T12:23:45.1234+01:00, true",
			"2014-03-01T12:23:45.12345+01:00, true",
			"2014-03-01T12:23:45.123456+01:00, true",
			"2014-03-01T12:23:45, false",
			"2014-03-01T12:23:45.123, false"
	})
	void shouldGenerateRegexForIso8601WithOffset(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.iso8601WithOffset().matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

	// BDD: should generate a regex for non blank string that matches correctly
	@Test
	void shouldGenerateRegexForNonBlank() {
		// given / when / then
		then(RegexPatterns.nonBlank().matcher("Not Empty").matches()).isTrue();
		then(RegexPatterns.nonBlank().matcher("").matches()).isFalse();
		then(RegexPatterns.nonBlank().matcher("    ").matches()).isFalse();
		then(RegexPatterns.nonBlank().matcher("\nFoo\nBar\n").matches()).isTrue();
		then(RegexPatterns.nonBlank().matcher("\r\n").matches()).isFalse();
		then(RegexPatterns.nonBlank().matcher(" \r\n\t").matches()).isFalse();
	}

	// BDD: should generate a regex for non empty string that matches correctly
	@Test
	void shouldGenerateRegexForNonEmpty() {
		// given / when / then
		then(RegexPatterns.nonEmpty().matcher("Not Empty").matches()).isTrue();
		then(RegexPatterns.nonEmpty().matcher("").matches()).isFalse();
		then(RegexPatterns.nonEmpty().matcher("  ").matches()).isTrue();
		then(RegexPatterns.nonEmpty().matcher("\nFoo\nBar\n").matches()).isTrue();
		then(RegexPatterns.nonEmpty().matcher("\r\n").matches()).isTrue();
		then(RegexPatterns.nonEmpty().matcher(" \r\n\t").matches()).isTrue();
	}

	// BDD: should generate a regex for enumerated value that matches correctly
	@ParameterizedTest(name = "[{index}] textToMatch={0}, shouldMatch={1}")
	@CsvSource({
			"foo, true",
			"bar, true",
			"baz, false"
	})
	void shouldGenerateRegexForAnyOf(String textToMatch, boolean shouldMatch) {
		// when
		boolean matches = RegexPatterns.anyOf("foo", "bar").matcher(textToMatch).matches();

		// then
		then(matches).isEqualTo(shouldMatch);
	}

}
