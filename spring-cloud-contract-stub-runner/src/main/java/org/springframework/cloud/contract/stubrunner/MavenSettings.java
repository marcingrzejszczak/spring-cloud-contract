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

import java.io.File;
import java.util.HashMap;

import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipher;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;

import org.springframework.util.StringUtils;

public class MavenSettings {

	private static final String MAVEN_USER_CONFIG_DIRECTORY = "maven.user.config.dir";

	private static final String SECURITY_XML = "settings-security.xml";

	private final String homeDir;

	public MavenSettings() {
		this(userSettings());
	}

	public MavenSettings(String homeDir) {
		this.homeDir = homeDir;
	}

	private static String fromSystemPropOrEnv(String prop) {
		String resolvedProp = System.getProperty(prop);
		if (StringUtils.hasText(resolvedProp)) {
			return resolvedProp;
		}
		return System.getenv(prop);
	}

	private static String userSettings() {
		String user = fromSystemPropOrEnv(MAVEN_USER_CONFIG_DIRECTORY);
		if (user == null) {
			return System.getProperty("user.home");
		}
		return user;
	}

	public SettingsDecrypter createSettingsDecrypter() {
		File file = new File(MavenSettings.this.homeDir, SECURITY_XML);
		String configurationFilePath = file.getAbsolutePath();
		return new DefaultSettingsDecrypter(
				new SpringCloudContractSecDispatcher(new DefaultPlexusCipher(), configurationFilePath));
	}

	private static class SpringCloudContractSecDispatcher extends DefaultSecDispatcher {

		SpringCloudContractSecDispatcher(PlexusCipher cipher, String configurationFile) {
			super(cipher, new HashMap<>(), configurationFile);
		}

	}

}
