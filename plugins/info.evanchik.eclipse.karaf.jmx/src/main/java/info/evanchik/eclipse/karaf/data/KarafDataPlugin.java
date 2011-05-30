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
package info.evanchik.eclipse.karaf.data;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafDataPlugin implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.jmx";

    private static KarafDataPlugin bundleActivator;

    private BundleContext bundleContext;

    public static KarafDataPlugin getDefault() {
        return bundleActivator;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        KarafDataPlugin.bundleActivator = this;
        this.bundleContext = context;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        KarafDataPlugin.bundleActivator = null;
        this.bundleContext = null;
    }
}
