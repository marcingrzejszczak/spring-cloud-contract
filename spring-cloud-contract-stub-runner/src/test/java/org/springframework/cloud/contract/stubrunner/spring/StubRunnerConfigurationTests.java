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

package org.springframework.cloud.contract.stubrunner.spring;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.HttpServerStubConfiguration;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.StubNotFoundException;
import org.springframework.cloud.contract.stubrunner.provider.wiremock.WireMockHttpServerStubAccessor;
import org.springframework.cloud.contract.stubrunner.provider.wiremock.WireMockHttpServerStubConfigurer;
import org.springframework.cloud.test.TestSocketUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@SpringBootTest(classes = StubRunnerConfigurationTests.Config.class, properties = {
		"stubrunner.cloud.enabled=false",
		"foo=${spring.cloud.contract.stubrunner.runningstubs.fraudDetectionServer.port}",
		"fooWithGroup=${spring.cloud.contract.stubrunner.runningstubs.org.springframework.cloud.contract.verifier.stubs.fraudDetectionServer.port}"
})
@AutoConfigureStubRunner(mappingsOutputFolder = "target/outputmappings/",
		httpServerStubConfigurer = StubRunnerConfigurationTests.HttpsForFraudDetection.class)
@ActiveProfiles("test")
class StubRunnerConfigurationTests {

	@Autowired
	StubFinder stubFinder;

	@Autowired
	Environment environment;

	@StubRunnerPort("fraudDetectionServer")
	int fraudDetectionServerPort;

	@StubRunnerPort("org.springframework.cloud.contract.verifier.stubs:fraudDetectionServer")
	int fraudDetectionServerPortWithGroupId;

	@Value("${foo}")
	Integer foo;

	@BeforeAll
	static void setupSpec() {
		System.clearProperty("spring.cloud.contract.stubrunner.repository.root");
		System.clearProperty("spring.cloud.contract.stubrunner.classifier");
		WireMockHttpServerStubAccessor.clear();
	}

	@AfterAll
	static void cleanupSpec() {
		setupSpec();
	}

	@Test
	void shouldMarkAllPortsAsRandom() {
		then(WireMockHttpServerStubAccessor.everyPortRandom()).isTrue();
	}

	@Test
	void shouldStartWireMockServers() throws IOException {
		then(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs", "loanIssuance")).isNotNull();
		then(stubFinder.findStubUrl("loanIssuance")).isNotNull();
		then(stubFinder.findStubUrl("loanIssuance"))
				.isEqualTo(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs", "loanIssuance"));
		then(stubFinder.findStubUrl("loanIssuance"))
				.isEqualTo(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs:loanIssuance"));
		then(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs:loanIssuance:0.0.1-SNAPSHOT"))
				.isEqualTo(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs:loanIssuance:0.0.1-SNAPSHOT:stubs"));
		then(stubFinder.findStubUrl("org.springframework.cloud.contract.verifier.stubs:fraudDetectionServer")).isNotNull();

		then(stubFinder.findAllRunningStubs().isPresent("loanIssuance")).isTrue();
		then(stubFinder.findAllRunningStubs().isPresent("org.springframework.cloud.contract.verifier.stubs", "fraudDetectionServer")).isTrue();
		then(stubFinder.findAllRunningStubs().isPresent("org.springframework.cloud.contract.verifier.stubs:fraudDetectionServer")).isTrue();

		URL loanIssuanceUrl = new URL(stubFinder.findStubUrl("loanIssuance").toString() + "/name");
		then(new String(loanIssuanceUrl.openStream().readAllBytes())).isEqualTo("loanIssuance");
		URL fraudDetectionUrl = new URL(stubFinder.findStubUrl("fraudDetectionServer").toString() + "/name");
		then(new String(fraudDetectionUrl.openStream().readAllBytes())).isEqualTo("fraudDetectionServer");

		then(stubFinder.findStubUrl("fraudDetectionServer").toString()).startsWith("https");
	}

	@Test
	void shouldThrowAnExceptionWhenStubIsNotFound() {
		thenThrownBy(() -> stubFinder.findStubUrl("nonExistingService")).isInstanceOf(StubNotFoundException.class);

		thenThrownBy(() -> stubFinder.findStubUrl("nonExistingGroupId", "nonExistingArtifactId"))
				.isInstanceOf(StubNotFoundException.class);
	}

	@Test
	void shouldRegisterStartedServersAsEnvironmentVariables() {
		then(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.loanIssuance.port")).isNotNull();
		then(stubFinder.findAllRunningStubs().getPort("loanIssuance"))
				.isEqualTo(Integer.parseInt(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.loanIssuance.port")));

		then(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.fraudDetectionServer.port")).isNotNull();
		then(stubFinder.findAllRunningStubs().getPort("fraudDetectionServer"))
				.isEqualTo(Integer.parseInt(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.fraudDetectionServer.port")));

		then(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.fraudDetectionServer.port")).isNotNull();
		then(stubFinder.findAllRunningStubs().getPort("fraudDetectionServer"))
				.isEqualTo(Integer.parseInt(environment.getProperty("spring.cloud.contract.stubrunner.runningstubs.org.springframework.cloud.contract.verifier.stubs.fraudDetectionServer.port")));
	}

	@Test
	void shouldBeAbleToInterpolateARunningStubInThePassedTestProperty() {
		int fraudPort = stubFinder.findAllRunningStubs().getPort("fraudDetectionServer");

		then(fraudPort).isGreaterThan(0);
		then(environment.getProperty("foo", Integer.class)).isEqualTo(fraudPort);
		then(environment.getProperty("fooWithGroup", Integer.class)).isEqualTo(fraudPort);
		then(foo).isEqualTo(fraudPort);
	}

	@Test
	void shouldBeAbleToRetrieveThePortOfARunningStubViaAnAnnotation() {
		int fraudPort = stubFinder.findAllRunningStubs().getPort("fraudDetectionServer");

		then(fraudPort).isGreaterThan(0);
		then(fraudDetectionServerPort).isEqualTo(fraudPort);
		then(fraudDetectionServerPortWithGroupId).isEqualTo(fraudPort);
	}

	@Test
	void shouldDumpAllMappingsToAFile() {
		URL url = stubFinder.findStubUrl("fraudDetectionServer");

		then(new File("target/outputmappings/", "fraudDetectionServer_" + url.getPort())).exists();
	}

	@Configuration
	@EnableAutoConfiguration
	static class Config {

	}

	static class HttpsForFraudDetection extends WireMockHttpServerStubConfigurer {

		private static final Log log = LogFactory.getLog(HttpsForFraudDetection.class);

		@Override
		public WireMockConfiguration configure(WireMockConfiguration httpStubConfiguration,
				HttpServerStubConfiguration httpServerStubConfiguration) {
			if ("fraudDetectionServer".equals(httpServerStubConfiguration.stubConfiguration.getArtifactId())) {
				int httpsPort = TestSocketUtils.findAvailableTcpPort();
				log.info("Will set HTTPs port [" + httpsPort + "] for fraud detection server");
				return httpStubConfiguration.httpsPort(httpsPort);
			}
			return httpStubConfiguration;
		}

	}

}
