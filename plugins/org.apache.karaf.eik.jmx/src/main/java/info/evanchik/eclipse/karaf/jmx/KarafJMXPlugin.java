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


import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafJMXPlugin implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.jmx";

    private static KarafJMXPlugin bundleActivator;

    private BundleContext bundleContext;

    private ServiceReference platformAdminRef;

    public static KarafJMXPlugin getDefault() {
        return bundleActivator;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public PlatformAdmin getPlatformAdmin() {
        final PlatformAdmin platAdmin = (PlatformAdmin) bundleContext.getService(platformAdminRef);
        if (platAdmin == null) {
            throw new AssertionError("PlatformAdmin service not available");
        }

        return platAdmin;
    }

    public void start(BundleContext context) throws Exception {
        KarafJMXPlugin.bundleActivator = this;
        this.bundleContext = context;
        this.platformAdminRef = bundleContext.getServiceReference(PlatformAdmin.class.getName());
        if (platformAdminRef == null) {
            throw new AssertionError("PlatformAdmin service not available");
        }
    }

    public void stop(BundleContext context) throws Exception {
        this.bundleContext.ungetService(platformAdminRef);

        KarafJMXPlugin.bundleActivator = null;
        this.bundleContext = null;
        this.platformAdminRef = null;
    }
}
