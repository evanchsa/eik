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
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.karaf.eclipse.core.AbstractStateBuilder;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.osgi.service.resolver.StateObjectFactory;
import org.osgi.framework.BundleException;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class StateBuilder extends AbstractStateBuilder {

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
    @Override
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
    @Override
    public void addAll(final Collection<File> bundles) {
        for (final File f : bundles) {
            add(f);
        }
    }

    /**
     *
     * @return
     */

    @Override
    public State build() {
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
}
