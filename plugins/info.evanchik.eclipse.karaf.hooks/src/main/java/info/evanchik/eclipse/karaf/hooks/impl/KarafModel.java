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
package info.evanchik.eclipse.karaf.hooks.impl;

import java.io.File;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafModel {

    /**
     * Karaf base directory, used to reference core Karaf system files; takes
     * precedence over Karaf home directory
     */
    public static final String KARAF_BASE_PROP = "karaf.base"; //$NON-NLS-1$

    /**
     * Environment variable for the Karaf base directory
     */
    public static final String KARAF_BASE_ENV = "KARAF_BASE"; //$NON-NLS-1$

    /**
     * Karaf home directory, used to reference installation specific files
     */
    public static final String KARAF_HOME_PROP = "karaf.home"; //$NON-NLS-1$

    /**
     * Environment variable for the Karaf home directory
     */
    public static final String KARAF_HOME_ENV = "KARAF_HOME"; //$NON-NLS-1$

    /**
     * The name of the file used to load the system properties
     */
    public static final String KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    /**
     * Searches for the Karaf base directory looking in the following locations:<br>
     * <ol>
     * <li>{@code karaf.base} system property</li>
     * <li>{@code KARAF_BASE} environment variable</li>
     * </ol>
     *
     * @see SystemPropertyLoader#KARAF_BASE_PROP
     * @see SystemPropertyLoader#KARAF_BASE_ENV
     *
     * @param defaultKarafBase
     *            the {@link File} to use as the default Karaf base directory if
     *            it is not specified as a system property or in the environment
     *
     * @return the {@link File} instance that points to the Karaf base directory
     */
    public static File getKarafBase(File defaultKarafBase) {
        File karafBase = null;

        String path = System.getProperty(KARAF_BASE_PROP);
        if (path != null) {
            karafBase = HookUtils.getCanonicalDirectory(path, "Invalid " + KARAF_BASE_PROP + "system property"); //$NON-NLS-1$ $NON-NLS-2$
        }

        if (karafBase == null) {
            path = System.getenv(KARAF_BASE_ENV);
            if (path != null) {
                karafBase = HookUtils.getCanonicalDirectory(path, "Invalid " + KARAF_BASE_ENV + "environment variable"); //$NON-NLS-1$ $NON-NLS-2$
            }
        }

        if (karafBase == null) {
            return defaultKarafBase;
        }

        return karafBase;
    }

    /**
     * Searches for the Karaf home directory looking in the following locations:<br>
     * <ol>
     * <li>{@code karaf.home} system property</li>
     * <li>{@code KARAF_HOME} environment variable</li>
     * </ol>
     *
     * @see SystemPropertyLoader#KARAF_HOME_PROP
     * @see SystemPropertyLoader#KARAF_HOME_ENV
     *
     * @return the {@link File} instance that points to the Karaf home directory
     */
    public static File getKarafHome() {
        File karafHome = null;

        String path = System.getProperty(KARAF_HOME_PROP);
        if (path != null) {
            karafHome = HookUtils.getCanonicalDirectory(path, "Invalid " + KARAF_HOME_PROP + "system property"); //$NON-NLS-1$ $NON-NLS-2$
        }

        if (karafHome == null) {
            path = System.getenv(KARAF_HOME_ENV);
            if (path != null) {
                karafHome = HookUtils.getCanonicalDirectory(path, "Invalid " + KARAF_HOME_ENV + "environment variable"); //$NON-NLS-1$ $NON-NLS-2$
            }
        }

        return karafHome;
    }

}
