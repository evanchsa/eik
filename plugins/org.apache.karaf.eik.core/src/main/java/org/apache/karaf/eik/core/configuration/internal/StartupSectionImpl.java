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
package org.apache.karaf.eik.core.configuration.internal;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eik.core.configuration.StartupSection;
import org.apache.karaf.eik.core.internal.KarafCorePluginActivator;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.service.resolver.BundleDescription;

public class StartupSectionImpl extends AbstractPropertiesConfigurationSection implements
        StartupSection {

    /**
     * Simple object that models a start level and {@link BundleDescription}
     * pair.
     */
    protected class BundleStartEntry {

        private final BundleDescription bundleDescription;

        private final String startLevel;

        BundleStartEntry(BundleDescription desc, String startLevel) {
            this.bundleDescription = desc;
            this.startLevel = startLevel;
        }

        BundleDescription getBundleDescription() {
            return bundleDescription;
        }

        String getStartLevel() {
            return startLevel;
        }
    }

    public static final String STARTUP_SECTION_ID = "org.apache.karaf.eik.configuration.section.Startup";

    public static final String STARTUP_FILENAME = "startup.properties";

    /**
     * The mapping of a bundle symbolic name to a {@link BundleStartEntry}
     */
    private final Map<String, BundleStartEntry> startupStateModel;

    /**
     * Constructor. This will build the necessary data objects to support
     * querying the Karaf target platform startup state model.
     *
     * @param karafModel the {@link KarafPlatformModel} that will be used to build the
     *                   startup state model
     */
    public StartupSectionImpl(KarafPlatformModel karafModel) {
        super(STARTUP_SECTION_ID, STARTUP_FILENAME, karafModel);

        this.startupStateModel = new LinkedHashMap<String, BundleStartEntry>(64);
    }

    public boolean containsPlugin(String bundleSymbolicName) {
        return startupStateModel.containsKey(bundleSymbolicName);
    }

    public String getStartLevel(String bundleSymbolicName) {
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
     * Converts a MVN url to a file path, relative to System.
     * Code taken from MvnUrlConverter from UI plugin.
     * TODO: move class MvnUrlConverter inside core plugin and use it
     * as dependency from UI plugin.
     */
    protected String getPath(String url) {
        if (url != null) {
            if (url.startsWith("mvn:")) {
                url = url.substring(4);
                String[] repositorySplit = url.split("!");
                String urlWithoutRepository = repositorySplit[repositorySplit.length - 1];

                String[] segments = urlWithoutRepository.split("/");
                if (segments.length >= 3) {
                    String groupId = segments[0];
                    String artifactId = segments[1];
                    String version = segments[2];

                    return groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
                }
            }
        }

        return url;
    }

    /**
     * Populates the startup state model with the {@link BundleDescriptions} of
     * the bundles found in the startup configuration.
     */
    protected void populateStartupStateModel() {
        final File rootBundleDir = getParent().getPluginRootDirectory().toFile();
        for (Object o : getProperties().keySet()) {
            // In karaf-3.0.0, mvn urls are sored instead of raw paths.
            // Then first try to convert mvn urls to raw path.
            final File bundleLocation = new File(rootBundleDir, getPath((String) o));

            final BundleDescription desc = getParent().getState().getBundleByLocation(
                    bundleLocation.getAbsolutePath());

            if (desc == null) {
                KarafCorePluginActivator.getLogger().error(
                        "Unable to locate bundle description for: "
                                + bundleLocation.getAbsolutePath());
            } else {
                final BundleStartEntry se = new BundleStartEntry(desc, (String) getProperties()
                        .get(o));
                startupStateModel.put(desc.getSymbolicName(), se);

            }
        }
    }

}
