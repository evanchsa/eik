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
package info.evanchik.eclipse.karaf.core.internal;

import info.evanchik.eclipse.karaf.core.LogWrapper;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafCorePluginActivator extends Plugin {

    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.core"; //$NON-NLS-1$

    private static KarafCorePluginActivator plugin = null;

    /**
     * Returns the shared instance of this plugin.
     *
     * @return the shared instance
     */
    public static KarafCorePluginActivator getDefault() {
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

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        KarafCorePluginActivator.plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        KarafCorePluginActivator.plugin = null;
    }
}
