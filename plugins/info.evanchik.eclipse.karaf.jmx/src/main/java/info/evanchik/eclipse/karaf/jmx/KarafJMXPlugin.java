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
public class KarafJMXPlugin implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.jmx";

    public static final String JMX_SERVER_PLUGIN_ID = "org.eclipse.equinox.jmx.server";

    public static final String JMX_SERVER_RMI_CONNECTOR_PLUGIN_ID = "org.eclipse.equinox.jmx.server.rmi";

    public static final String JMX_COMMON_PLUGIN_ID = "org.eclipse.equinox.jmx.common";

    private static BundleActivator bundleActivator;

    private BundleContext bundleContext;

    public static BundleActivator getDefault() {
        return bundleActivator;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void start(BundleContext context) throws Exception {
        KarafJMXPlugin.bundleActivator = this;
        this.bundleContext = context;
    }

    public void stop(BundleContext context) throws Exception {
        KarafJMXPlugin.bundleActivator = null;
        this.bundleContext = null;
    }
}
