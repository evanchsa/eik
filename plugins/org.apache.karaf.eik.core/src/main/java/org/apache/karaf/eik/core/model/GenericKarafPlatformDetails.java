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
package org.apache.karaf.eik.core.model;

import org.apache.karaf.eik.core.KarafPlatformDetails;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

public class GenericKarafPlatformDetails implements KarafPlatformDetails {

    private static final String UNKNOWN_VERSION = "Unknown version";

    private static final String UNKNOWN_PLATFORM = "Unknown platform";

    private final JarFile platformDetailsBundle;

    public GenericKarafPlatformDetails(final File file) throws IOException {
        this.platformDetailsBundle = new JarFile(file);
    }

    @Override
    public String getDescription() {
        final String description;
        try {
            final Manifest manifest = platformDetailsBundle.getManifest();
            if (manifest == null) {
                description = "";
            } else {
                final String bundleDescription = manifest.getMainAttributes().getValue(Constants.BUNDLE_DESCRIPTION);
                if (bundleDescription == null) {
                    description = "";
                } else {
                    description = bundleDescription;
                }
            }
        } catch (final IOException e) {
            return "";
        }

        return description;
    }

    @Override
    public String getName() {
        final String name;
        try {
            final Manifest manifest = platformDetailsBundle.getManifest();
            if (manifest == null) {
                name = UNKNOWN_PLATFORM;
            } else {
                final String bundleName = manifest.getMainAttributes().getValue(Constants.BUNDLE_NAME);
                if (bundleName == null) {
                    name = UNKNOWN_PLATFORM;
                } else {
                    name = bundleName;
                }
            }
        } catch (final IOException e) {
            return UNKNOWN_PLATFORM;
        }

        return name;
    }

    @Override
    public String getVersion() {
        final String version;
        try {
            final Manifest manifest = platformDetailsBundle.getManifest();
            if (manifest == null) {
                version = UNKNOWN_VERSION;
            } else {
                final String bundleVersion = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
                if (bundleVersion == null) {
                    version = UNKNOWN_VERSION;
                } else {
                    version = bundleVersion;
                }
            }
        } catch (final IOException e) {
            return UNKNOWN_VERSION;
        }
        return version;
    }

}
