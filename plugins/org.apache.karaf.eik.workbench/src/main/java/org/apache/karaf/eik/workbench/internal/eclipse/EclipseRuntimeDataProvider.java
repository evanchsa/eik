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
package org.apache.karaf.eik.workbench.internal.eclipse;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.provider.AbstractRuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProviderListener;
import org.apache.karaf.eik.workbench.provider.ServiceItem;

import java.util.EnumSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

public class EclipseRuntimeDataProvider extends AbstractRuntimeDataProvider implements RuntimeDataProvider {

    private final BundleContext context;

    private final BundleListener eclipseBundleListener;

    private final AllServiceListener eclipseAllServiceListener;

    private ServiceReference packageAdminReference;

    private PackageAdmin packageAdmin;

    private ServiceReference startLevelReference;

    private StartLevel startLevel;

    private final class EclipseBundleListener implements BundleListener {

        @Override
        public void bundleChanged(BundleEvent event) {
            final BundleItem wrappedBundle = new BundleItem(event.getBundle(), startLevel, packageAdmin);

            EnumSet<RuntimeDataProviderListener.EventType> events = EnumSet.noneOf(RuntimeDataProviderListener.EventType.class);

            switch (event.getType()) {
            case BundleEvent.INSTALLED:
                synchronized (bundleSet) {
                    if (!bundleSet.contains(wrappedBundle)) {
                        bundleSet.add(wrappedBundle);

                        events = EnumSet.of(RuntimeDataProviderListener.EventType.ADD);
                    }
                }
                break;
            case BundleEvent.UNINSTALLED:
                synchronized (bundleSet) {
                    if (bundleSet.contains(wrappedBundle)) {
                        bundleSet.remove(wrappedBundle);

                        events = EnumSet.of(RuntimeDataProviderListener.EventType.REMOVE);
                    }
                }
                break;
            default:
                events = EnumSet.of(RuntimeDataProviderListener.EventType.CHANGE);
                break;
            }

            fireProviderChangeEvent(events);
        }
    };

    private final class EclipseAllServiceListener implements AllServiceListener {

        @Override
        public void serviceChanged(ServiceEvent event) {
            final ServiceReference reference = event.getServiceReference();

            final ServiceItem ri = new ServiceItem(reference);

            EnumSet<RuntimeDataProviderListener.EventType> events = EnumSet.noneOf(RuntimeDataProviderListener.EventType.class);

            switch (event.getType()) {
            case ServiceEvent.REGISTERED:
                synchronized (serviceSet) {
                    if (serviceSet.contains(ri)) {
                        serviceSet.add(ri);
                        events = EnumSet.of(RuntimeDataProviderListener.EventType.ADD);
                    }
                }
                break;
            case ServiceEvent.UNREGISTERING:
                synchronized (serviceSet) {
                    if (serviceSet.contains(ri)) {
                        serviceSet.remove(ri);
                        events = EnumSet.of(RuntimeDataProviderListener.EventType.REMOVE);
                    }
                }
                break;
            case ServiceEvent.MODIFIED:
            default:
                events = EnumSet.of(RuntimeDataProviderListener.EventType.CHANGE);
                break;
            }

            fireProviderChangeEvent(events);
        }
    };

    public EclipseRuntimeDataProvider(BundleContext context) {
        super();

        this.context = context;

        this.eclipseBundleListener = new EclipseBundleListener();
        this.eclipseAllServiceListener = new EclipseAllServiceListener();

        this.packageAdminReference = context.getServiceReference(PackageAdmin.class.getName());
        this.packageAdmin = (PackageAdmin) context.getService(packageAdminReference);

        this.startLevelReference = context.getServiceReference(StartLevel.class.getName());
        this.startLevel = (StartLevel) context.getService(startLevelReference);
    }

    public String getName() {
        return "Eclipse Workbench";
    }

    public void start() {
        context.addBundleListener(eclipseBundleListener);
        context.addServiceListener(eclipseAllServiceListener);

        final Job initializeJob = new Job("Populating OSGi Runtime view data for: Eclipse Workbench") {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                if (monitor == null) {
                    monitor = new NullProgressMonitor();
                }

                final Bundle[] bundles;
                final ServiceReference[] services;

                try {
                    bundles = context.getBundles();
                    services = context.getAllServiceReferences(null, null);
                } catch (InvalidSyntaxException e) {
                    // TODO: Log something?
                    return new Status(IStatus.ERROR, KarafWorkbenchActivator.PLUGIN_ID,
                            "Unable to  get service references for running Eclipse Workbench", e);
                }

                int servicesLength = 0;
                if (services != null) {
                    servicesLength = services.length;
                }

                monitor.worked(10);

                monitor.beginTask("Populating view data set", bundles.length + servicesLength);

                monitor.subTask("OSGi Bundles");

                for (Bundle b : bundles) {
                    /*
                     * This bundle can't be added because it does not have a
                     * context
                     */
                    if (b == null || b.getBundleContext() == null) {
                        continue;
                    }

                    synchronized (bundleSet) {
                        bundleSet.add(new BundleItem(b, startLevel, packageAdmin));
                    }

                    monitor.worked(1);
                }

                // If there are no services then this is null
                if (services != null) {
                    monitor.subTask("OSGi Services");

                    for (ServiceReference r : services) {
                        synchronized (serviceSet) {
                            serviceSet.add(new ServiceItem(r));
                        }

                        monitor.worked(1);
                    }
                }

                monitor.done();

                return Status.OK_STATUS;
            }
        };

        initializeJob.setPriority(Job.LONG);
        initializeJob.schedule();

        fireStartEvent();
    }

    public void stop() {
        context.removeBundleListener(eclipseBundleListener);
        context.removeServiceListener(eclipseAllServiceListener);

        context.ungetService(startLevelReference);
        startLevel = null;
        startLevelReference = null;

        context.ungetService(packageAdminReference);
        packageAdmin = null;
        packageAdminReference = null;

        synchronized (bundleSet) {
            bundleSet.clear();
            serviceSet.clear();
        }

        fireStopEvent();
    }

}
