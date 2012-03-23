/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ops4j.pax.url.mvn;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.ops4j.pax.url.maven.commons.MavenConfigurationImpl;
import org.ops4j.pax.url.maven.commons.MavenSettingsImpl;
import org.ops4j.pax.url.mvn.internal.Connection;
import org.ops4j.util.property.PropertiesPropertyResolver;

public class MvnURLConnectionFactory {

    private final Properties configuration;

    /**
     * Constructs the factory using a {@link Properties} to configure how PAX
     * Maven URL resolves Maven artifacts
     *
     * @param configuration
     *            the {@code Properties} used to configure the
     *            {@code URLConnection}
     */
    public MvnURLConnectionFactory(final Properties configuration) {
        this.configuration = configuration;
    }

    /**
     * Creates a {@link URLConnection} from the specified {@link URL}
     *
     * @param url
     *            the {@code URL}
     * @return the {@code URLConnection}
     * @throws IOException
     */
    public URLConnection create(final URL url) throws IOException {
        final PropertiesPropertyResolver systemProperties =
                new PropertiesPropertyResolver(System.getProperties());

        final PropertiesPropertyResolver configuredProperties =
                new PropertiesPropertyResolver(configuration, systemProperties);

        final MavenConfigurationImpl config =
                new MavenConfigurationImpl(configuredProperties, ServiceConstants.PID);

        config.setSettings(new MavenSettingsImpl(config.getSettingsFileUrl(), config.useFallbackRepositories()));

        return new Connection(url, config);
    }
}
