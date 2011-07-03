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




/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafLaunchConfigurationConstants {

    /**
     * The list of features to use during Karaf boot
     */
    public static final String KARAF_LAUNCH_BOOT_FEATURES = "karaf_boot_features"; //$NON-NLS-1$

    /**
     * The workspace project that contains all of the configuration data for the
     * launch configuration
     */
    public static final String KARAF_LAUNCH_CONFIGURATION_PROJECT = "karaf_configuration_project"; //$NON-NLS-1$

    /**
     * Determines whether or not the Karaf Features system is managed by the
     * Eclipse launch configuration
     */
    public static final String KARAF_LAUNCH_FEATURES_MANAGEMENT = "karaf_features_management"; //$NON-NLS-1$

    /**
     * PDE Launcher constant used for recording classpath entries used as part
     * of the boot classpath for Karaf
     */
    public static final String KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH = "karaf_required_boot_classpath"; //$NON-NLS-1$

    /**
     * Contains the root directory of the Karaf platform that this launch
     * configuration is configured against
     */
    public static final String KARAF_LAUNCH_SOURCE_RUNTIME = "karaf_source_runtime"; //$NON-NLS-1$

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
     * The password used to authenticate to the remote console
     */
    public static final String KARAF_REMOTE_CONSOLE_PASSWORD = "karaf_remote_console_password"; //$NON-NLS-1$

    /**
     * The username used to authenticate to the remote console
     */
    public static final String KARAF_REMOTE_CONSOLE_USERNAME = "karaf_remote_console_username"; //$NON-NLS-1$

    /**
     * Private constructor to prevent instantiation.
     */
    private KarafLaunchConfigurationConstants() {
        throw new AssertionError(KarafLaunchConfigurationConstants.class.getName() + " cannot be instantiated");
    }
}
