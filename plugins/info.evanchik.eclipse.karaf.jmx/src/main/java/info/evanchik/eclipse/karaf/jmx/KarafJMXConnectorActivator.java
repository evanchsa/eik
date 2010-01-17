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
package info.evanchik.eclipse.karaf.jmx;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafJMXConnectorActivator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.jmx";

    /**
     * Constructor.
     */
    public KarafJMXConnectorActivator() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {

        if (!verifyBaseEnvironment()) {
            // throw some exception
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
    }

    /**
     * Determines if the base environment is setup properly. This includes:<br>
     * <ol>
     * <li></li>
     * </ol>
     *
     * @return
     */
    private boolean verifyBaseEnvironment() {
        return true;
    }
}
