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
package info.evanchik.eclipse.karaf.core;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IKarafConstants {

    /**
     * Karaf base directory, used to reference core Karaf system files; takes
     * precedence over Karaf home directory
     */
    public static final String KARAF_BASE_PROP = "karaf.base"; //$NON-NLS-1$

    public static final String KARAF_BUNDLE_LOCATIONS_PROP = "bundle.locations"; //$NON-NLS-1$

    public static final String KARAF_DATA_PROP = "karaf.data"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_BUNDLE_START_LEVEL = "100"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_CONFIG_PROPERTIES_FILE = "config.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME = "info.evanchik.eclipse.karaf.target"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_STARTUP_PROPERTIES_FILE = "startup.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    /**
     * Karaf home directory, used to reference installation specific files
     */
    public static final String KARAF_HOME_PROP = "karaf.home"; //$NON-NLS-1$

    public static final String KARAF_INSTANCES_PROP = "karaf.instances"; //$NON-NLS-1$

    public static final String KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.jaas.boot";

    public static final String KARAF_MAIN_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.main";

    public static final String KARAF_OSGI_FRAMEWORK_ID = "info.evanchik.eclipse.karaf.Framework"; //$NON-NLS-1$

    public static final String ORG_APACHE_FELIX_KARAF_MANAGEMENT_CFG_FILENAME = "org.apache.felix.karaf.management.cfg"; //$NON-NLS-1$

    public static final String ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME = "org.apache.karaf.management.cfg"; //$NON-NLS-1$

    public static final String ORG_APACHE_SERVICEMIX_MANAGEMENT_CFG_FILENAME = "org.apache.servicemix.management.cfg"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel base
     * directory.
     *
     * @see IKarafConstants#KARAF_BASE_PROP
     */
    public static final String SERVICEMIX_BASE_PROP = "servicemix.base"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel home
     * directory.
     *
     * @see IKarafConstants#KARAF_HOME_PROP
     */
    public static final String SERVICEMIX_HOME_PROP = "servicemix.home"; //$NON-NLS-1$
}
