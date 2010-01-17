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
package info.evanchik.eclipse.karaf.core.jmx;

import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.jmx.Constants;
import org.osgi.jmx.compendium.ConfigAdminManagerMBean;
import org.osgi.jmx.core.BundleStateMBean;
import org.osgi.jmx.core.FrameworkMBean;
import org.osgi.jmx.core.PackageStateMBean;
import org.osgi.jmx.core.ServiceStateMBean;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafMBeanProvider implements MBeanProvider, NotificationListener {

    private static final ObjectName BUNDLE_STATE;

    private static final ObjectName CM_SERVICE;

    private static final ObjectName FRAMEWORK;

    private static final ObjectName PACKAGE_STATE;

    private static final ObjectName SERVICE_STATE;

    /*
     * If this throws an exception we're in trouble because it means that the
     * constants in {@link org.osgi.jmx.Constants} are invalid
     */
    static {
        try {
            BUNDLE_STATE = new ObjectName(Constants.BUNDLE_STATE);
            CM_SERVICE = new ObjectName(Constants.CM_SERVICE);
            FRAMEWORK = new ObjectName(Constants.FRAMEWORK);
            PACKAGE_STATE = new ObjectName(Constants.PACKAGE_STATE);
            SERVICE_STATE = new ObjectName(Constants.SERVICE_STATE);
        } catch (Exception e) {
            throw new IllegalStateException("The OSGi JMX implementation references an invalid ObjectName", e);
        }
    }

    private final JMXConnector connector;

    private final MBeanServerConnection mbeanServer;

    private final ExecutorService connectionHandler;

    private BundleStateMBean bundleStateMBean;

    private ConfigAdminManagerMBean configAdminManagerMBean;

    private FrameworkMBean frameworkMBean;

    private PackageStateMBean packageStateMBean;

    private ServiceStateMBean serviceStateMBean;

    private Object memento;

    private ServiceRegistration mbeanProviderService;

    private ServiceRegistration bundleStateService;

    private ServiceRegistration configAdminManagerService;

    private ServiceRegistration frameworkService;

    private ServiceRegistration packageStateService;

    private ServiceRegistration serviceStateService;

    /**
     * Constructs an {@link MBeanProvider} that opens a connection to a
     * {@link MBeanServerConnection}
     *
     * @param connector
     *            the {@link JMXConnector} that represents the JMX connection
     * @throws IOException
     *             if the connection to the MBean Server cannot be made
     */
    public KarafMBeanProvider(JMXConnector connector) throws IOException {
        this.connector = connector;
        this.mbeanServer = connector.getMBeanServerConnection();

        this.connectionHandler = Executors.newSingleThreadExecutor();

        this.connector.addConnectionNotificationListener(this, null, null);
    }

    public void close() {

        // JMXConnector.close() is a potentially long running operation.
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    connector.removeConnectionNotificationListener(KarafMBeanProvider.this);
                    connector.close();
                } catch (IOException e) {
                    KarafCorePluginActivator.getLogger().info("Unable to close connection to JMX MBeanServer", e);
                } catch (ListenerNotFoundException e) {
                    // Nothing to do since this is impossible
                }
            }
        };

        connectionHandler.submit(r);

        if (isOpen()) {
            unregisterServices();
        }
    }

    public BundleStateMBean getBundleStateMBean() {
        return bundleStateMBean;
    }

    public FrameworkMBean getFrameworkMBean() {
        return frameworkMBean;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mbeanServer;
    }

    public PackageStateMBean getPackageStateMBean() {
        return packageStateMBean;
    }

    public ServiceStateMBean getServiceStateMBean() {
        return serviceStateMBean;
    }

    public void handleNotification(Notification notification, Object handback) {
    }

    /**
     * Determines if this provider is open by examining if a memento has been
     * set.
     *
     * @return true if the memento has been set, false otherwise
     */
    public boolean isOpen() {
        return memento != null;
    }

    public void open(Object memento) {
        if (memento == null) {
            throw new NullPointerException("Cannot specify a null memento");
        }

        if (isOpen()) {
            // Log something at Trace level?
            return;
        }

        this.memento = memento;

        bundleStateMBean = (BundleStateMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, BUNDLE_STATE, BundleStateMBean.class, false);
        configAdminManagerMBean = (ConfigAdminManagerMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanServer, CM_SERVICE, ConfigAdminManagerMBean.class, false);
        frameworkMBean = (FrameworkMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanServer, FRAMEWORK, FrameworkMBean.class, false);
        packageStateMBean = (PackageStateMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanServer, PACKAGE_STATE, PackageStateMBean.class, false);
        serviceStateMBean = (ServiceStateMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanServer, SERVICE_STATE, ServiceStateMBean.class, false);

        registerServices();
    }

    private void registerServices() {
        final BundleContext bundleContext = KarafCorePluginActivator.getDefault().getBundle().getBundleContext();

        final Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
        dictionary.put(MBeanProvider.KARAF_WORKBENCH_SERVICES_ID, memento);

        mbeanProviderService = bundleContext.registerService(MBeanProvider.class.getName(), this, dictionary);

        bundleStateService = bundleContext.registerService(BundleStateMBean.class.getName(), bundleStateMBean, dictionary);
        configAdminManagerService = bundleContext.registerService(ConfigAdminManagerMBean.class.getName(), configAdminManagerMBean, dictionary);
        frameworkService = bundleContext.registerService(FrameworkMBean.class.getName(), frameworkMBean, dictionary);
        packageStateService = bundleContext.registerService(PackageStateMBean.class.getName(), packageStateMBean, dictionary);
        serviceStateService = bundleContext.registerService(ServiceStateMBean.class.getName(), serviceStateMBean, dictionary);
    }

    private void unregisterServices() {
        mbeanProviderService.unregister();
        mbeanProviderService = null;

        bundleStateService.unregister();
        bundleStateService = null;

        configAdminManagerService.unregister();
        configAdminManagerService = null;

        frameworkService.unregister();
        frameworkService = null;

        packageStateService.unregister();
        packageStateService = null;

        serviceStateService.unregister();
        serviceStateService = null;
    }
}
