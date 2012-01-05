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
package info.evanchik.eclipse.karaf.core.configuration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface StartupSection extends ConfigurationSection {

    /**
     * Determines if the specified bundle will be started by Karaf during its
     * initialization.
     *
     * @param bundleSymbolicName
     *            the symbolic name of the bundle
     * @return true if the plugin is listed in the startup configuration for
     *         Karaf, false otherwise
     */
    public boolean containsPlugin(String bundleSymbolicName);

    /**
     * Getter for the start level of the bundle specified by the symbolic name.
     *
     * @param bundleSymbolicName
     *            the symbolic name of the bundle
     * @return the start level of the bundle, null if the bundle does not exist
     *         in the system startup configuration.
     */
    public String getStartLevel(String bundleSymbolicName);
}
