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
package org.apache.karaf.eclipse.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.osgi.service.resolver.StateObjectFactory;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class StateBuilder {

    private static final long ADD_NEW_BUNDLE_ID = -1;

    private final StateObjectFactory stateObjectFactory;

    private final AtomicLong nextBundleId = new AtomicLong();

    private final State state;

    /**
     *
     */
    public StateBuilder() {
        this(false);
    }

    /**
     *
     * @param resolve
     */
    public StateBuilder(final boolean resolve) {
        stateObjectFactory = Platform.getPlatformAdmin().getFactory();
        state = stateObjectFactory.createState(resolve);
    }

    /**
     *
     * @param bundleLocation
     * @return
     */
    public boolean add(final File bundleLocation) {
        try {
            final Map<Object, Object> manifest = loadManifest(bundleLocation);

            if (isBundle(manifest)) {
                final BundleDescription bundleDescription = addBundle(manifest, bundleLocation, ADD_NEW_BUNDLE_ID);

                return bundleDescription != null;
            } else {
                return false;
            }
        } catch (final IOException e) {
            // Intentionally left blank
        }

        return false;
    }

    /**
     *
     * @param bundles
     */
    public void addAll(final Collection<File> bundles) {
        for (final File f : bundles) {
            add(f);
        }
    }

    /**
     *
     * @return
     */
    public State getState() {
        return state;
    }

    /**
     *
     * @param manifest
     * @param bundleLocation
     * @param requestedBundleId
     * @return
     */
    private BundleDescription addBundle(final Map<Object, Object> manifest, final File bundleLocation, final long requestedBundleId) {
        try {
            final Hashtable<Object, Object> dictionary = new Hashtable<Object, Object>();
            dictionary.putAll(manifest);

            final long bundleId;
            if (requestedBundleId == ADD_NEW_BUNDLE_ID) {
                bundleId = nextBundleId.incrementAndGet();
            } else {
                bundleId = requestedBundleId;
            }

            final BundleDescription descriptor =
                stateObjectFactory.createBundleDescription(
                    state,
                    dictionary,
                    bundleLocation.getAbsolutePath(),
                    bundleId);

            if (ADD_NEW_BUNDLE_ID == requestedBundleId) {
                state.addBundle(descriptor);
            } else if (!state.updateBundle(descriptor)) {
                state.addBundle(descriptor);
            }

            return descriptor;
        } catch (final BundleException e) {
            // Intentionally left blank
        } catch (final NumberFormatException e) {
            // Intentionally left blank
        } catch (final IllegalArgumentException e) {
            // Intentionally left blank
        }

        return null;
    }

    /**
     *
     * @param manifest
     * @return
     */
    private String getBundleSymbolicName(final Map<?, ?> manifest) {
        return (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME);
    }

    /**
     *
     * @param manifest
     * @return
     */
    private boolean isBundle(final Map<?, ?> manifest) {
        return manifest != null && getBundleSymbolicName(manifest) != null;
    }

    /**
     *
     * @param bundleLocation
     * @return
     */
    private boolean isSupportedArchive(final File bundleLocation) {
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
    private Map<Object, Object> loadManifest(final File bundleLocation) throws IOException {
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
            @SuppressWarnings("unchecked")
            final Map<Object, Object> theMap = ManifestElement.parseBundleManifest(manifestStream, new HashMap<Object, Object>());
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
