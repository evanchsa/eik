/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
