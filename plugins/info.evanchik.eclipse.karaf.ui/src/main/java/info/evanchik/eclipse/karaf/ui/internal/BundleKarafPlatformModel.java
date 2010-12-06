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
package info.evanchik.eclipse.karaf.ui.internal;

import info.evanchik.eclipse.karaf.core.model.AbstractKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BundleKarafPlatformModel extends AbstractKarafPlatformModel {

    public static final String KARAF_RUNTIME_BUNDLES_LOCATION = "/runtimes/karaf/plugins"; //$NON-NLS-1$

    public static final String KARAF_BOOT_CLASSPATH_LOCATION = "/runtimes/karaf/bootclasspath"; //$NON-NLS-1$

    public static final String KARAF_CONFIGURATION_LOCATION = "/runtimes/karaf/etc"; //$NON-NLS-1$

    /**
     * Reference to the Karaf Platform definition bundle
     */
    private final Bundle karafRuntimeBundle;

    /**
     * Constructor.
     *
     * @param karafRuntime
     *            the bundle that contains the Karaf runtime definition.
     */
    public BundleKarafPlatformModel(Bundle karafRuntime) {
        this.karafRuntimeBundle = karafRuntime;
    }

    public List<String> getBootClasspath() {
        @SuppressWarnings("unchecked")
        final Enumeration<URL> classpathEntries = karafRuntimeBundle.findEntries(
                        BundleKarafPlatformModel.KARAF_BOOT_CLASSPATH_LOCATION, "*.jar", false); //$NON-NLS-1$

        final List<String> classpathList = new ArrayList<String>();
        while (classpathEntries.hasMoreElements()) {
            URL bundleEntry = classpathEntries.nextElement();

            try {
                bundleEntry = FileLocator.toFileURL(bundleEntry);
                classpathList.add(bundleEntry.getPath());
            } catch (IOException e) {
                KarafUIPluginActivator.getLogger().error(
                                "Unable to resolve Karaf bootclasspath bundle URL to file URL", e);
            }
        }

        return classpathList;
    }

    /**
     * Retrieves the path to the configuration directory inside the bundle that
     * provides the Karaf Platform.
     *
     * @return the absolute path to the configuration directory provided by the
     *         bundle this model represents.
     */
    public IPath getConfigurationDirectory() {
        return getRootDirectory().append(BundleKarafPlatformModel.KARAF_CONFIGURATION_LOCATION);
    }

    /**
     * The configuration files provided by the bundle are simply appended to the
     * configuration directory path as returned by
     * {@link BundleKarafPlatformModel#getConfigurationDirectory()}
     *
     * @return the absolute path to the configuration file indicated by the key
     */
    public IPath getConfigurationFile(String key) {
        return getConfigurationDirectory().append(key);
    }

    /**
     * Retrieves absolute path to the plugins provided by this Karaf platform
     * instance.
     *
     * @return the absolute path to the plugins provided by this Karaf platform
     *         instance
     */
    public IPath getPluginRootDirectory() {
        return getRootDirectory().append(BundleKarafPlatformModel.KARAF_RUNTIME_BUNDLES_LOCATION);
    }

    /**
     * Getter for the root directory of this bundle in the form of an
     * {@link IPath}.<br>
     * <br>
     * If there is a problem resolving the bundle to an {@code IPath} then this
     * method returns {@code new Path("")}
     */
    public IPath getRootDirectory() {
        File f;
        try {
            f = FileLocator.getBundleFile(karafRuntimeBundle);
            return new Path(f.getAbsolutePath());
        } catch (IOException e) {
            KarafUIPluginActivator.getLogger().error(
                            "Unable to resolve bundle to file: " + karafRuntimeBundle.toString());
            return new Path(""); //$NON-NLS-1$
        }
    }

    /**
     * Bundle based Karaf platform models are read-only as they are really
     * template platform models.
     *
     * @return true in all cases
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Given that this type of Karaf target platform model is created by a
     * programmer it is assumed to be valid.<br>
     * <br>
     * This assumption probably should be revisited.
     *
     * @return true in all cases
     */
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<URL> getPlatformBundles() {
        @SuppressWarnings("unchecked")
        final Enumeration<URL> classpathEntries = karafRuntimeBundle.findEntries(
                        BundleKarafPlatformModel.KARAF_RUNTIME_BUNDLES_LOCATION, "*.jar", false); //$NON-NLS-1$

        final List<URL> bundleList = new ArrayList<URL>();
        while (classpathEntries.hasMoreElements()) {
            final URL bundleEntry = classpathEntries.nextElement();

            try {
                bundleList.add(FileLocator.toFileURL(bundleEntry));
            } catch (IOException e) {
                KarafUIPluginActivator.getLogger().error("Unable to resolve URL to File", e);
            }
        }

        return bundleList;
    }
}
