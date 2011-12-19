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
package org.apache.karaf.eclipse.ui.configuration.internal;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.ui.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eclipse.ui.configuration.StartupSection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.service.resolver.BundleDescription;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class StartupSectionImpl extends AbstractPropertiesConfigurationSection implements
                StartupSection {

    /**
     * Simple object that models a start level and {@link BundleDescription}
     * pair.
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    protected class BundleStartEntry {

        private final BundleDescription bundleDescription;

        private final String startLevel;

        public BundleStartEntry(final BundleDescription desc, final String startLevel) {
            this.bundleDescription = desc;
            this.startLevel = startLevel;
        }

        public BundleDescription getBundleDescription() {
            return bundleDescription;
        }

        public String getStartLevel() {
            return startLevel;
        }
    }

    public static final String STARTUP_SECTION_ID = "org.apache.karaf.eclipse.configuration.section.Startup";

    public static final String STARTUP_FILENAME = "startup.properties";

    /**
     * The mapping of a bundle symbolic name to a {@link BundleStartEntry}
     */
    private final Map<String, BundleStartEntry> startupStateModel;

    /**
     * Constructor. This will build the necessary data objects to support
     * querying the Karaf target platform startup state model.
     *
     * @param karafModel
     *            the {@link KarafPlatformModel} that will be used to build the
     *            startup state model
     */
    public StartupSectionImpl(final KarafPlatformModel karafModel) {
        this(karafModel, STARTUP_FILENAME);
    }

    /**
     *
     * @param karafModel
     * @param filename
     */
    public StartupSectionImpl(final KarafPlatformModel karafModel, final String filename) {
    	super(STARTUP_SECTION_ID, filename, karafModel);

        this.startupStateModel = new LinkedHashMap<String, BundleStartEntry>(64);

    }

    @Override
    public boolean containsPlugin(final String bundleSymbolicName) {
        return startupStateModel.containsKey(bundleSymbolicName);
    }

    @Override
    public String getStartLevel(final String bundleSymbolicName) {
        final BundleStartEntry se = startupStateModel.get(bundleSymbolicName);

        if (se == null) {
            return null;
        }

        return se.getStartLevel();
    }

    /**
     * Loads the startup configuration and then initializes the bundle startup
     * state from the processed configuration.
     *
     * @return {@link IStatus#OK} if successful, {@link IStatus#ERROR} otherwise
     */
    @Override
    public IStatus load() {
        final IStatus status = super.load();

        populateStartupStateModel();

        return status;
    }

    /**
     * Populates the startup state model with the {@link BundleDescriptions} of
     * the bundles found in the startup configuration.
     */
    protected void populateStartupStateModel() {
        final File rootBundleDir = getParent().getPluginRootDirectory().toFile();
        for (final Object o : getProperties().keySet()) {
            final File bundleLocation = new File(rootBundleDir, (String) o);

            final BundleDescription desc = getParent().getState().getBundleByLocation(
                            bundleLocation.getAbsolutePath());

            if (desc == null) {
                KarafUIPluginActivator.getLogger().error(
                                "Unable to locate bundle description for: "
                                                + bundleLocation.getAbsolutePath());
            } else {
                final BundleStartEntry se = new BundleStartEntry(desc, (String) getProperties().get(o));
                addBundleStartEntry(desc.getSymbolicName(), se);
            }
        }
    }

	/**
	 * Adds a {@link BundleStartEntry} to this {@code StartupSection}
	 *
	 * @param symbolicName
	 *            the Bundle's Symbolic Name
	 * @param bundleStartEntry
	 *            the {@code BundleStartEntry}
	 */
    protected final void addBundleStartEntry(final String symbolicName, final BundleStartEntry bundleStartEntry) {
        startupStateModel.put(symbolicName, bundleStartEntry);
    }
}
