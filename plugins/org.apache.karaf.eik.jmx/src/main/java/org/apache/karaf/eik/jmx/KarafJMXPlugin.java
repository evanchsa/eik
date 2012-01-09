/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.jmx;

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
    public static final String PLUGIN_ID = "org.apache.karaf.eik.jmx";

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
