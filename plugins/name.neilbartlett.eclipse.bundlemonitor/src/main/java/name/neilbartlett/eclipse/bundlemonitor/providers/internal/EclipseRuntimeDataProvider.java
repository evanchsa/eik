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
package name.neilbartlett.eclipse.bundlemonitor.providers.internal;

import info.evanchik.eclipse.karaf.workbench.provider.AbstractRuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.OSGiServiceWrapper;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProviderListener;

import java.util.EnumSet;

import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;

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
import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class EclipseRuntimeDataProvider extends AbstractRuntimeDataProvider implements BundleListener, AllServiceListener {

    private final BundleContext context;

    private ServiceReference packageAdminReference;

    private PackageAdmin packageAdmin;

    private ServiceReference startLevelReference;

    private StartLevel startLevel;

    public EclipseRuntimeDataProvider(BundleContext context) {
        super();

        this.context = context;

        this.packageAdminReference = context.getServiceReference(PackageAdmin.class.getName());
        this.packageAdmin = (PackageAdmin) context.getService(packageAdminReference);

        this.startLevelReference = context.getServiceReference(StartLevel.class.getName());
        this.startLevel = (StartLevel) context.getService(startLevelReference);
    }

    public void bundleChanged(BundleEvent event) {
        final OSGiBundle wrappedBundle = new OSGiBundle(event.getBundle().getBundleContext(), packageAdmin, startLevel, event.getBundle());

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

    public String getName() {
        return "Eclipse Workbench";
    }

    public void serviceChanged(ServiceEvent event) {
        final ServiceReference reference = event.getServiceReference();

        final OSGiBundle bundle = new EclipseOSGiBundleWrapper(reference.getBundle().getBundleContext(), packageAdmin, startLevel,
                reference.getBundle());
        final OSGiServiceWrapper ri = new EclipseOSGiServiceWrapper(reference, bundle);

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

    public void start() {
        context.addBundleListener(this);
        context.addServiceListener(this);

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
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
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
                        bundleSet.add(new EclipseOSGiBundleWrapper(b.getBundleContext(), packageAdmin, startLevel, b));
                    }

                    monitor.worked(1);
                }

                // If there are no services then this is null
                if (services != null) {
                    monitor.subTask("OSGi Services");

                    for (ServiceReference r : services) {
                        final OSGiBundle bundle = new EclipseOSGiBundleWrapper(r.getBundle().getBundleContext(), packageAdmin, startLevel,
                                r.getBundle());

                        synchronized (serviceSet) {
                            serviceSet.add(new EclipseOSGiServiceWrapper(r, bundle));
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
        context.removeBundleListener(this);
        context.removeServiceListener(this);

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
