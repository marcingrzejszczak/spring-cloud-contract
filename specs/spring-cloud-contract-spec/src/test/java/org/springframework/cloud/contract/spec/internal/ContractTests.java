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

import org.springframework.cloud.contract.spec.Contract;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

/**
 * @author Marcin Grzejszczak
 */
class ContractTests {

	@Test
	void shouldWorkForHttp() {
		// when
		Contract contract = Contract.make(c -> {
			c.request(r -> {
				r.url("/foo");
				r.method("PUT");
				r.headers(h -> {
					h.header("foo", "bar");
				});
				r.body(java.util.Map.of("foo", "bar"));
			});
			c.response(r -> {
				r.status(200);
				r.headers(h -> {
					h.header("foo2", "bar");
				});
				r.body(java.util.Map.of("foo2", "bar"));
			});
		});

		// then
		then(contract).isNotNull();
	}

	@Test
	void shouldFailWhenNoMethodIsPresent() {
		// when / then
		thenThrownBy(() -> {
			Contract contract = Contract.make(c -> {
				c.request(r -> {
					r.url("/foo");
				});
				c.response(r -> {
					r.status(200);
				});
			});
			Contract.assertContract(contract);
		})
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Method is missing for HTTP contract");
	}

	@Test
	void shouldFailWhenNoUrlIsPresent() {
		// when / then
		thenThrownBy(() -> {
			Contract contract = Contract.make(c -> {
				c.request(r -> {
					r.method("GET");
				});
				c.response(r -> {
					r.status(200);
				});
			});
			Contract.assertContract(contract);
		})
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("URL is missing for HTTP contract");
	}

	@Test
	void shouldFailWhenNoStatusIsPresent() {
		// when / then
		thenThrownBy(() -> {
			Contract contract = Contract.make(c -> {
				c.request(r -> {
					r.url("/foo");
					r.method("GET");
				});
				c.response(r -> {
					// No status
				});
			});
			Contract.assertContract(contract);
		})
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Status is missing for HTTP contract");
	}

	// BDD: should set a description
	@Test
	void shouldSetADescription() {
		// given / when
		// tag::description[]
		Contract contract = Contract.make(c -> {
			c.description("""
given:
	An input
when:
	Sth happens
then:
	Output
""");
		});
		// end::description[]

		// then
		then(contract).isNotNull();
		then(contract.getDescription()).isNotBlank();
	}

	// BDD: should set a name
	@Test
	void shouldSetAName() {
		// given / when
		// tag::name[]
		Contract contract = Contract.make(c -> {
			c.name("some_special_name");
		});
		// end::name[]

		// then
		then(contract).isNotNull();
		then(contract.getName()).isEqualTo("some_special_name");
	}

	// BDD: should mark a contract ignored
	@Test
	void shouldMarkAContractIgnored() {
		// given / when
		// tag::ignored[]
		Contract contract = Contract.make(c -> {
			c.ignored();
		});
		// end::ignored[]

		// then
		then(contract).isNotNull();
		then(contract.isIgnored()).isTrue();
	}

	// BDD: should mark a contract in progress
	@Test
	void shouldMarkAContractInProgress() {
		// given / when
		// tag::in_progress[]
		Contract contract = Contract.make(c -> {
			c.inProgress();
		});
		// end::in_progress[]

		// then
		then(contract).isNotNull();
		then(contract.isInProgress()).isTrue();
	}

	// BDD: should make equals and hashcode work properly for URL
	@Test
	void shouldMakeEqualsAndHashcodeWorkProperlyForUrl() {
		// given
		Contract a = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/1");
			});
		});
		Contract b = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/1");
			});
		});

		// when / then
		then(a).isEqualTo(b);
		then(a.hashCode()).isEqualTo(b.hashCode());
	}

	// BDD: should make equals and hashcode work properly for URL with consumer producer
	@Test
	void shouldMakeEqualsAndHashcodeWorkProperlyForUrlWithConsumerProducer() {
		// given
		Contract a = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url(r.$(r.c("/1"), r.p("/1")));
			});
		});
		Contract b = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url(r.$(r.c("/1"), r.p("/1")));
			});
		});

		// when / then
		then(a).isEqualTo(b);
	}

	// BDD: should return true when comparing two equal contracts with gstring
	@Test
	void shouldReturnTrueWhenComparingTwoEqualContractsWithGstring() {
		// given
		int index = 1;
		Contract a = Contract.make(c -> {
			c.request(r -> {
				r.method(r.PUT());
				r.headers(h -> {
					h.contentType(h.applicationJson());
				});
				r.url("/" + index);
			});
			c.response(r -> {
				r.status(r.OK());
			});
		});
		Contract b = Contract.make(c -> {
			c.request(r -> {
				r.method(r.PUT());
				r.headers(h -> {
					h.contentType(h.applicationJson());
				});
				r.url("/" + index);
			});
			c.response(r -> {
				r.status(r.OK());
			});
		});

		// when / then
		then(a.getRequest().getMethod()).isEqualTo(b.getRequest().getMethod());
		then(a.getRequest().getUrl()).isEqualTo(b.getRequest().getUrl());
		then(a.getRequest().getHeaders().getEntries().iterator().next())
			.isEqualTo(b.getRequest().getHeaders().getEntries().iterator().next());
		then(a.getRequest().getHeaders().getEntries()).isEqualTo(b.getRequest().getHeaders().getEntries());
		then(a.getRequest().getHeaders()).isEqualTo(b.getRequest().getHeaders());
		then(a.getRequest()).isEqualTo(b.getRequest());
		then(a.getResponse().getStatus()).isEqualTo(b.getResponse().getStatus());
		then(a.getResponse()).isEqualTo(b.getResponse());
		then(a).isEqualTo(b);
	}

	// BDD: should return false when comparing two unequal contracts with gstring
	@Test
	void shouldReturnFalseWhenComparingTwoUnequalContractsWithGstring() {
		// given
		int index = 1;
		Contract a = Contract.make(c -> {
			c.request(r -> {
				r.method(r.PUT());
				r.headers(h -> {
					h.contentType(h.applicationJson());
				});
				r.url("/" + index);
			});
			c.response(r -> {
				r.status(r.OK());
			});
		});
		int index2 = 2;
		Contract b = Contract.make(c -> {
			c.request(r -> {
				r.method(r.PUT());
				r.headers(h -> {
					h.contentType(h.applicationJson());
				});
				r.url("/" + index2);
			});
			c.response(r -> {
				r.status(r.OK());
			});
		});

		// when / then
		then(a).isNotEqualTo(b);
	}

	// BDD: should return true when comparing two equal complex contracts
	@Test
	void shouldReturnTrueWhenComparingTwoEqualComplexContracts() {
		// given
		Contract a = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/path");
				r.headers(h -> {
					h.header("Accept", r.$(
							r.consumer(r.regex("text/.*")),
							r.producer("text/plain")
					));
					h.header("X-Custom-Header", r.$(
							r.consumer(r.regex("^.*2134.*$")),
							r.producer("121345")
					));
				});
			});
			c.response(r -> {
				r.status(r.OK());
				r.body(java.util.Map.of(
						"id", java.util.Map.of("value", "132"),
						"surname", "Kowalsky",
						"name", "Jan",
						"created", "2014-02-02 12:23:43"
				));
				r.headers(h -> {
					h.header("Content-Type", "text/plain");
				});
			});
		});
		Contract b = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/path");
				r.headers(h -> {
					h.header("Accept", r.$(
							r.consumer(r.regex("text/.*")),
							r.producer("text/plain")
					));
					h.header("X-Custom-Header", r.$(
							r.consumer(r.regex("^.*2134.*$")),
							r.producer("121345")
					));
				});
			});
			c.response(r -> {
				r.status(r.OK());
				r.body(java.util.Map.of(
						"id", java.util.Map.of("value", "132"),
						"surname", "Kowalsky",
						"name", "Jan",
						"created", "2014-02-02 12:23:43"
				));
				r.headers(h -> {
					h.header("Content-Type", "text/plain");
				});
			});
		});

		// when / then
		then(a.getRequest().getMethod()).isEqualTo(b.getRequest().getMethod());
		then(a.getRequest().getUrl()).isEqualTo(b.getRequest().getUrl());
		
		var aFirstHeader = a.getRequest().getHeaders().getEntries().iterator().next();
		var bFirstHeader = b.getRequest().getHeaders().getEntries().iterator().next();
		then(aFirstHeader).isEqualTo(bFirstHeader);
		
		var aHeadersList = new java.util.ArrayList<>(a.getRequest().getHeaders().getEntries());
		var bHeadersList = new java.util.ArrayList<>(b.getRequest().getHeaders().getEntries());
		var aLastHeader = aHeadersList.get(aHeadersList.size() - 1);
		var bLastHeader = bHeadersList.get(bHeadersList.size() - 1);
		then(aLastHeader).isEqualTo(bLastHeader);
		
		then(a.getRequest().getHeaders().getEntries()).isEqualTo(b.getRequest().getHeaders().getEntries());
		then(a.getRequest().getHeaders()).isEqualTo(b.getRequest().getHeaders());
		then(a.getRequest().getBody()).isEqualTo(b.getRequest().getBody());
		then(a.getRequest()).isEqualTo(b.getRequest());
		then(a.getResponse().getStatus()).isEqualTo(b.getResponse().getStatus());
		
		var aRespFirstHeader = a.getResponse().getHeaders().getEntries().iterator().next();
		var bRespFirstHeader = b.getResponse().getHeaders().getEntries().iterator().next();
		then(aRespFirstHeader).isEqualTo(bRespFirstHeader);
		
		then(a.getResponse().getBody()).isEqualTo(b.getResponse().getBody());
		then(a.getResponse()).isEqualTo(b.getResponse());
		then(a).isEqualTo(b);
	}

	// BDD: should work with optional and null value of a field
	@Test
	void shouldWorkWithOptionalAndNullValueOfAField() {
		// given / when
		Contract contract = Contract.make(c -> {
			c.description("Creating user");
			c.name("Create user");
			c.request(r -> {
				r.method("POST");
				r.url("/api/user");
				r.body(java.util.Map.of(
						"address", r.$(r.consumer(r.optional(r.regex(r.alphaNumeric()))), r.producer(null)),
						"name", r.$(r.consumer(r.optional(r.regex(r.alphaNumeric()))), r.producer(""))
				));
				r.headers(h -> {
					h.contentType(h.applicationJson());
				});
			});
			c.response(r -> {
				r.status(201);
			});
		});

		// then
		then(contract).isNotNull();
	}

	// BDD: should fail when regex do not match the concrete value (Issue #1200)
	@Test
	void shouldFailWhenRegexDoNotMatchTheConcreteValue() {
		// when / then
		thenThrownBy(() -> Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/any");
			});
			c.response(r -> {
				r.status(r.OK());
				r.body(java.util.Map.of(
						"time", r.$(r.producer(r.regex(r.iso8601WithOffset())), r.consumer("thisIsNotADate"))
				));
			});
		}))
			.isInstanceOf(IllegalStateException.class);
	}

	// BDD: should work fine when dealing with anyOf (Issue #1215)
	@Test
	void shouldWorkFineWhenDealingWithAnyOf() {
		// when
		Contract contract = Contract.make(c -> {
			c.request(r -> {
				r.method("GET");
				r.url("/any");
				r.body(java.util.Map.of(
						"foo", r.$(r.consumer(r.optional(r.anyOf("WORKS", "MIGHTY", "DESPAIR"))), r.producer("DESPAIR"))
				));
			});
			c.response(r -> {
				r.status(r.OK());
			});
		});

		// then
		then(contract).isNotNull();
	}
}
