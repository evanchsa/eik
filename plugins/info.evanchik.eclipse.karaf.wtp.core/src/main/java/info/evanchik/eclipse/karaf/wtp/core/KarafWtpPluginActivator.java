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
package info.evanchik.eclipse.karaf.wtp.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafWtpPluginActivator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.wtp.core";

    /**
     * The list of runtime type identifiers that this plugin defines
     */
    public static final String[] RUNTIME_TYPE_IDS = new String[] { "info.evanchik.eclipse.server.karaf.runtime.12" };

    // The shared instance
    private static KarafWtpPluginActivator plugin;

    /**
     * The constructor
     */
    public KarafWtpPluginActivator() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KarafWtpPluginActivator getDefault() {
        return plugin;
    }

}
