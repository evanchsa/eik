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
package info.evanchik.eclipse.karaf.core.model;

import info.evanchik.eclipse.karaf.core.KarafPlatformDetails;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformDetails implements KarafPlatformDetails {

    private static final String UNKNOWN_VERSION = "Unknown version";

    private static final String UNKNOWN_PLATFORM = "Unknown platform";

    private final JarFile platformDetailsBundle;

    /**
     *
     * @param file
     */
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
