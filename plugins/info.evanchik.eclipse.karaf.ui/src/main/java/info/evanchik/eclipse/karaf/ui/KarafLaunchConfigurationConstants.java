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
package info.evanchik.eclipse.karaf.ui;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafLaunchConfigurationConstants {

    /**
     * PDE Launcher constant used for determining if the local console should
     * start
     */
    public static final String KARAF_LAUNCH_START_LOCAL_CONSOLE = "karaf_start_local_console"; //$NON-NLS-1$

    /**
     * PDE Launcher constant used for determining if the remote console should
     * start
     */
    public static final String KARAF_LAUNCH_START_REMOTE_CONSOLE = "karaf_start_remote_console"; //$NON-NLS-1$

    /**
     * PDE Launcher constant used for recording classpath entries used as part
     * of the boot classpath for Karaf
     */
    public static final String KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH = "karaf_required_boot_classpath"; //$NON-NLS-1$

    /**
     * PDE Launcher constant that contains the value for karaf.base
     */
    public static final String KARAF_LAUNCH_KARAF_HOME = "karaf_base"; //$NON-NLS-1$

    /**
     * PDE Launcher constant that contains the value for karaf.home
     */
    public static final String KARAF_LAUNCH_KARAF_BASE = "karaf_home"; //$NON-NLS-1$

    /**
     * Contains the root directory that this launch configuration will use for
     * its source configuration data
     */
    public static final String KARAF_LAUNCH_SOURCE_RUNTIME = "karaf_source_runtime"; //$NON-NLS-1$

    /**
     * The workspace project that contains all of the configuration data for the
     * launch configuration
     */
    public static final String KARAF_LAUNCH_CONFIGURATION_PROJECT = "karaf_configuration_project"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel base
     * directory.
     *
     * @see KarafPlatformModel#KARAF_BASE_PROP
     */
    public static final String SERVICEMIX_BASE_PROP = "servicemix.base"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel home
     * directory.
     *
     * @see KarafPlatformModel#KARAF_HOME_PROP
     */
    public static final String SERVICEMIX_HOME_PROP = "servicemix.home"; //$NON-NLS-1$

    /**
     * Private constructor to prevent instantiation.
     */
    private KarafLaunchConfigurationConstants() {
        throw new IllegalStateException(KarafLaunchConfigurationConstants.class.getName() + " cannot be instantiated");
    }
}
