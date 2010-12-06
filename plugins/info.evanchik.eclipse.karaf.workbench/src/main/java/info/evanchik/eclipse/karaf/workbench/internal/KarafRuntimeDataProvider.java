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
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.MBeanProvider;
import info.evanchik.eclipse.karaf.workbench.provider.AbstractRuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProviderListener;
import info.evanchik.eclipse.karaf.workbench.provider.ServiceItem;

import java.io.IOException;
import java.util.EnumSet;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.apache.aries.jmx.codec.ServiceData;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafRuntimeDataProvider extends AbstractRuntimeDataProvider {

    private static final ObjectName BUNDLE_STATE;

    @SuppressWarnings("unused")
    private static final ObjectName CM_SERVICE;

    @SuppressWarnings("unused")
    private static final ObjectName PACKAGE_STATE;

    private static final ObjectName SERVICE_STATE;

    /*
     * If this throws an exception we're in trouble because it means that the
     * constants are invalid
     */
    static {
        try {
            BUNDLE_STATE = new ObjectName("osgi.core:type=bundleState,version=1.5");
            CM_SERVICE = new ObjectName("osgi.core:type=cm,version=1.3");
            PACKAGE_STATE = new ObjectName("osgi.core:type=packageState,version=1.5");
            SERVICE_STATE = new ObjectName("osgi.core:type=serviceState,version=1.5");
        } catch (Exception e) {
            throw new IllegalStateException("The OSGi JMX implementation references an invalid ObjectName", e);
        }
    }

    private final String name;

    private final MBeanProvider mbeanProvider;

    /**
     * Updates the runtime data referenced by the {@link BundleStateMBean} and
     * {@link ServiceStateMBean}
     */
    private final Job updateDataJob;

    /**
     * A {@link RuntimeDataProvider} implementation specific to Karaf instances
     * being run/debugged in Eclipse.
     *
     * @param name
     *            the human readable name of this provider
     * @param provider
     *            the {@link MBeanProvider} for the running Karaf instance
     */
    public KarafRuntimeDataProvider(String name, MBeanProvider provider) {
        super();

        this.name = name;
        this.mbeanProvider = provider;

        this.updateDataJob = new Job("Populating OSGi Runtime view data for: " + name) {

            private volatile boolean cancel = false;

            @Override
            public IStatus run(IProgressMonitor monitor) {
                if (monitor == null) {
                    monitor = new NullProgressMonitor();
                }

                monitor.beginTask("Populating view data set", IProgressMonitor.UNKNOWN);

                IStatus status = loadBundleData(monitor);
                if (!status.isOK()) {
                    // Do something?
                }

                if (monitor.isCanceled()) {
                    return Status.OK_STATUS;
                }

                status = loadServiceData(monitor);
                if (!status.isOK()) {
                    // Do something?
                }

                monitor.done();

                if (monitor.isCanceled()) {
                    return Status.OK_STATUS;
                }

                if (!cancel) {
                    /*
                     * Poll every 25 seconds because the MBeans are not notification
                     * enabled
                     */
                    this.schedule(25000);
                }

                fireProviderChangeEvent(EnumSet.of(RuntimeDataProviderListener.EventType.CHANGE));

                return Status.OK_STATUS;
            }

            @Override
            protected void canceling() {
                cancel = true;
            }
        };

        this.updateDataJob.setSystem(true);
        this.updateDataJob.setPriority(Job.LONG);
    }

    @Override
    public Image getIcon() {
        return KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.LOGO_16X16_IMG);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Starts the {@link RuntimeDataProvider} which will collect the OSGi
     * runtime information from the running Karaf instance.
     */
    @Override
    public void start() {
        fireStartEvent();

        updateDataJob.schedule();
    }

    @Override
    public void stop() {
        updateDataJob.cancel();

        synchronized (bundleSet) {
            bundleSet.clear();
            idToBundleMap.clear();
        }

        synchronized (serviceSet) {
            serviceSet.clear();
            idToServiceMap.clear();
        }

        fireStopEvent();
    }

    private IStatus loadBundleData(IProgressMonitor monitor) {
        monitor.subTask("OSGi Bundles");

        if (monitor.isCanceled()) {
            return Status.OK_STATUS;
        }

        if (!mbeanProvider.isOpen()) {
            return new Status(IStatus.WARNING, KarafWorkbenchActivator.PLUGIN_ID, "Connection to MBean server has been closed");
        }

        try {
            final TabularData rawBundleData = mbeanProvider.getMBean(BUNDLE_STATE, BundleStateMBean.class).listBundles();

            synchronized (bundleSet) {
                bundleSet.clear();
                idToBundleMap.clear();
            }

            for (Object o : rawBundleData.values()) {
                final CompositeData composite = (CompositeData) o;
                final BundleItem bundle = new BundleItem(composite);

                synchronized (bundleSet) {
                    bundleSet.add(bundle);
                    idToBundleMap.put(bundle.getIdentifier(), bundle);
                }

                monitor.worked(1);

                if (monitor.isCanceled()) {
                    break;
                }

            }

        } catch (IOException e) {
            return new Status(IStatus.ERROR, KarafWorkbenchActivator.PLUGIN_ID, "Unable to connect to MBeanServer", e);
        } catch (Exception e) {
            KarafWorkbenchActivator.getLogger().warn("Unable to update OSGi Bundles", e);
        }

        return Status.OK_STATUS;
    }

    private IStatus loadServiceData(IProgressMonitor monitor) {
        monitor.subTask("OSGi Services");

        if (monitor.isCanceled()) {
            return Status.OK_STATUS;
        }

        if (!mbeanProvider.isOpen()) {
            return new Status(IStatus.WARNING, KarafWorkbenchActivator.PLUGIN_ID, "Connection to MBean server has been closed");
        }

        try {
            final TabularData rawServiceData = mbeanProvider.getMBean(SERVICE_STATE, ServiceStateMBean.class).listServices();

            synchronized (serviceSet) {
                serviceSet.clear();
                idToServiceMap.clear();
            }

            for (Object o : rawServiceData.values()) {
                final CompositeData composite = (CompositeData) o;
                final ServiceData service = ServiceData.from(composite);

                /*
                 * Get the service's properties from the JMX enabled runtime
                 */
                if (!mbeanProvider.isOpen()) {
                    return new Status(IStatus.WARNING, KarafWorkbenchActivator.PLUGIN_ID, "Connection to MBean server has been closed");
                }

                final ServiceItem serviceWrapper = new MBeanServiceItem(composite, mbeanProvider, idToBundleMap);

                synchronized (serviceSet) {
                    serviceSet.add(serviceWrapper);
                    idToServiceMap.put(service.getServiceId(), serviceWrapper);
                }

                monitor.worked(1);

                if (monitor.isCanceled()) {
                    break;
                }

            }
        } catch (IOException e) {
            return new Status(IStatus.ERROR, KarafWorkbenchActivator.PLUGIN_ID, "Unable to connect to MBeanServer", e);
        } catch (Exception e) {
            KarafWorkbenchActivator.getLogger().warn("Unable to update OSGi Services", e);
        }

        return Status.OK_STATUS;
    }

}
