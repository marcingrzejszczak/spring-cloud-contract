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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.junit.jupiter.api.Test;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.BDDAssertions.then;

class GitStubDownloaderPropertiesTests {

	@Test
	void shouldParseOnlyTheUrlAfterProtocolIfItDoesntStartWithGit() {
		Resource resource = resource("git://https://foo.com");

		GitStubDownloaderProperties props = new GitStubDownloaderProperties(resource,
				new StubRunnerOptionsBuilder().build());

		then(props.url).isEqualTo(URI.create("https://foo.com"));
	}

	@Test
	void shouldReturnTheWholeAddressIfItStartsWithGitButDoesntFinishWithDotGit() {
		Resource resource = resource("git://git@foo.com/foo");

		GitStubDownloaderProperties props = new GitStubDownloaderProperties(resource,
				new StubRunnerOptionsBuilder().build());

		then(props.url).isEqualTo(URI.create("git:git@foo.com/foo"));
	}

	private Resource resource(String resourceUri) {
		return new AbstractResource() {
			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}

			@Override
			public URI getURI() throws IOException {
				return URI.create(resourceUri);
			}
		};
	}

}
