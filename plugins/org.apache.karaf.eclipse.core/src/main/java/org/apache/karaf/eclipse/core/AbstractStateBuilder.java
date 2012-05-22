/**
 * Copyright (c) 2012 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package org.apache.karaf.eclipse.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractStateBuilder extends PlatformObject implements IKarafStateBuilder {

    protected AbstractStateBuilder() {
    }

    /**
     *
     * @param manifest
     * @return
     */
    protected final String getBundleSymbolicName(final Map<?, ?> manifest) {
        return (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME);
    }

    /**
     *
     * @param manifest
     * @return
     */
    protected final boolean isBundle(final Map<?, ?> manifest) {
        return manifest != null && getBundleSymbolicName(manifest) != null;
    }

    /**
     *
     * @param bundleLocation
     * @return
     */
    protected boolean isSupportedArchive(final File bundleLocation) {
        if (!bundleLocation.isFile()) {
            return false;
        } else {
            final String extension = new Path(bundleLocation.getName()).getFileExtension();
            return "jar".equals(extension) || "war".equals(extension); //$NON-NLS-1$ $NON-NLS-2$
        }
    }

    /**
     *
     * @param bundleLocation
     * @return
     * @throws IOException
     */
    protected final Map<String, String> loadManifest(final File bundleLocation) throws IOException {
        ZipFile jarFile = null;

        InputStream manifestStream = null;

        try {
            if (isSupportedArchive(bundleLocation)) {
                jarFile = new ZipFile(bundleLocation, ZipFile.OPEN_READ);
                final ZipEntry manifestEntry = jarFile.getEntry(JarFile.MANIFEST_NAME);
                if (manifestEntry != null) {
                    manifestStream = jarFile.getInputStream(manifestEntry);
                }
            } else {
                final File file = new File(bundleLocation, JarFile.MANIFEST_NAME);
                if (file.exists()) {
                    manifestStream = new FileInputStream(file);
                }
            }
        } catch (final IOException e) {
            // Intentionally left blank
        }

        if (manifestStream == null) {
            return null;
        }

        try {
            @SuppressWarnings(value = { "unchecked" })
            final Map<String, String> theMap = ManifestElement.parseBundleManifest(manifestStream, new HashMap<String, String>());
            return theMap;
        } catch (final BundleException e) {
            // Intentionally left blank
        } finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (final IOException e) {
            }
        }

        return null;
    }
}
