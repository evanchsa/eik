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

import org.apache.karaf.eik.core.LogWrapper;

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
    public static final String[] RUNTIME_TYPE_IDS = new String[] {
        "info.evanchik.eclipse.karaf.wtp.server.runtime.2"
    };

    // The shared instance
    private static KarafWtpPluginActivator plugin;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KarafWtpPluginActivator getDefault() {
        return plugin;
    }

    /**
     * Getter for the {@link LogWrapper} object that makes logging much easier
     * on the caller.
     *
     * @return the {@link LogWrapper} instance
     */
    public static LogWrapper getLogger() {
        return new LogWrapper(getDefault().getLog(), PLUGIN_ID);
    }

    /**
     * The constructor
     */
    public KarafWtpPluginActivator() {
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

}
