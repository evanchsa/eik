/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.core.model;

import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.karaf.eclipse.core.KarafPlatformDetails;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Constants;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformDetails implements KarafPlatformDetails {

    static final String UNKNOWN_VERSION = "Unknown version";

    static final String UNKNOWN_PLATFORM = "Unknown platform";

    private final String description;

    private final String name;

    private final String version;

    /**
     *
     *
     * @param jarFilePath
     * @return
     * @throws IOException
     */
    static GenericKarafPlatformDetails create(final IPath jarFilePath) throws IOException {
        final JarFile platformDetailsBundle = new JarFile(jarFilePath.toFile());
        try {
            final Manifest manifest = platformDetailsBundle.getManifest();
            if (manifest == null) {
                return new GenericKarafPlatformDetails(UNKNOWN_PLATFORM, UNKNOWN_PLATFORM, UNKNOWN_VERSION);
            } else {
                final String bundleName = safeGetString(manifest.getMainAttributes().getValue(Constants.BUNDLE_NAME), UNKNOWN_PLATFORM);
                final String bundleDescription = safeGetString(manifest.getMainAttributes().getValue(Constants.BUNDLE_DESCRIPTION), UNKNOWN_PLATFORM);
                final String bundleVersion = safeGetString(manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION), UNKNOWN_VERSION);

                return new GenericKarafPlatformDetails(bundleName, bundleDescription, bundleVersion);
            }
        } finally {
            try {
                if (platformDetailsBundle != null) {
                    platformDetailsBundle.close();
                }
            } catch (final IOException ex) {
                // Intentionally left blank
            }
        }
    }

    /**
     * Constructor
     *
     * @param name
     *            the name of the Karaf platform
     * @param description
     *            the description of the Karaf platform
     * @param version
     *            the version of the Karaf platform
     */
    GenericKarafPlatformDetails(final String name, final String description, final String version) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (description == null) {
            throw new NullPointerException("description");
        }

        if (version == null) {
            throw new NullPointerException("version");
        }

        this.name = name;
        this.description = description;
        this.version = version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Examines the requested {@code String} input for {@code null} values and
     * returns the non-{@code null} fallback value
     *
     * @param requestedValue
     *            a potentially {@code null} {@code String}
     * @param fallbackValue
     *            the non-{@code null} alternative if the requested
     *            {@code String} is {@code null}
     * @return a {@code String} and never {@code null}
     */
    private static String safeGetString(final String requestedValue, final String fallbackValue) {
        assert fallbackValue != null;

        if (requestedValue == null) {
            return fallbackValue;
        } else {
            return requestedValue;
        }
    }
}
