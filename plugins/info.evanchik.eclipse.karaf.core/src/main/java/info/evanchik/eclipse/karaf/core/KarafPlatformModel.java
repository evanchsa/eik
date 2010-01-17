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
package info.evanchik.eclipse.karaf.core;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafPlatformModel {

    /**
     * Karaf base directory, used to reference core Karaf system files; takes
     * precedence over Karaf home directory
     */
    public static final String KARAF_BASE_PROP = "karaf.base"; //$NON-NLS-1$

    /**
     * Karaf home directory, used to reference installation specific files
     */
    public static final String KARAF_HOME_PROP = "karaf.home"; //$NON-NLS-1$

    public static final String KARAF_BUNDLE_LOCATIONS_PROP = "bundle.locations"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_CONFIG_PROPERTIES_FILE = "config.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_STARTUP_PROPERTIES_FILE = "startup.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_BUNDLE_START_LEVEL = "100"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME = "info.evanchik.eclipse.karaf.target"; //$NON-NLS-1$

    public static final String KARAF_MAIN_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.main";

    public static final String KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.jaas.boot";

    /**
     * Determines if a plugin is in the Karaf Target Platform.
     *
     * @param plugin
     *            the plugin's descriptor
     * @return true if the plugin is in the Karaf Target Platform, false
     *         otherwise
     */
    public boolean containsPlugin(IPluginModelBase plugin);

    /**
     * Gets the list of boot classpath jars for Karaf
     *
     * @return a list of jars to be used on the boot classpath
     */
    public List<String> getBootClasspath();

    /**
     * Getter for the directory that contains the default configuration files.
     * These files are considered templates that will be used in the initial
     * configuration of the launch configurations.
     *
     * @return the {@link IPath} to the configuration directory
     */
    public IPath getConfigurationDirectory();

    /**
     * Getter for the root directory of Karaf platform
     *
     * @return the {@link IPath} to the root directory of the Karaf platform
     *         this model represents.
     */
    public IPath getRootDirectory();

    /**
     * Getter for the configuration file indicated by the supplied key. This is
     * typically the name of the file but can be anything as long as it is
     * unique among the set of identifiers used in configuration retrieval by
     * implementors of this interface.
     *
     * @param key
     *            the key of the configuration file
     * @return the {@link IPath} to the configuration file
     */
    public IPath getConfigurationFile(String key);

    /**
     * Gets the root directory that contains the plugins for the platform
     *
     * @return a {@link IPath} that represents the directory that contains the
     *         platform bundles
     */
    public IPath getPluginRootDirectory();

    /**
     * Getter for the underlying OSGi {@link State} object that contains the
     * detailed information about the plugins found in this platform.
     *
     * @return the {@link State} object containing detailed model metadata
     */
    public State getState();

    /**
     * Gets the deployment directory for user deployed bundles. This typically
     * corresponds to the {@code KARAF_ROOT/deploy} directory.
     *
     * @return the {@link IPath} the the directory containing user deployed
     *         bundles
     */
    public IPath getUserDeployedDirectory();

    /**
     * Determines if the specified {@link IPluginModelBase} is a OSGi Framework
     * provider
     *
     * @param model
     *            the {@link IPluginModelBase} to evaluate
     * @return true if the plugin model is an OSGi Framework provider
     */
    public boolean isFrameworkPlugin(IPluginModelBase model);

    /**
     * Determines if the the Karaf platform model is read only.<br>
     * <br>
     * A read only Karaf platform means configuration files cannot be updated.
     *
     * @return true if configuration this Karaf platform instance supports
     *         writable configuration files
     */
    public boolean isReadOnly();

    /**
     * Determines if the Karaf platform model is valid and complete.
     *
     * @return true if the platform is valid, false otherwise
     */
    public boolean isValid();
}
