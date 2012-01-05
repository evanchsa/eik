/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.configuration.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.AbstractPropertiesConfigurationSection;
import info.evanchik.eclipse.karaf.core.configuration.StartupSection;
import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static final String STARTUP_SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.Startup";

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
     * Populates the startup state model with the {@link BundleDescriptions} of
     * the bundles found in the startup configuration.
     */
    protected void populateStartupStateModel() {
        final File rootBundleDir = getParent().getPluginRootDirectory().toFile();
        for (Object o : getProperties().keySet()) {
            final File bundleLocation = new File(rootBundleDir, (String) o);

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
