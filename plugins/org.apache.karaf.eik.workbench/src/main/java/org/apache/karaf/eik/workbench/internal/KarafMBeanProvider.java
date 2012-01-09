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
package org.apache.karaf.eik.workbench.internal;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.MBeanProvider;
import org.apache.karaf.eik.workbench.jmx.JMXServiceDescriptor;

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

import org.eclipse.core.runtime.PlatformObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class KarafMBeanProvider extends PlatformObject implements MBeanProvider, NotificationListener {

    private final JMXConnector connector;

    private final ExecutorService connectionHandler;

    private final JMXServiceDescriptor jmxServiceDescriptor;

    private final MBeanServerConnection mbeanServer;

    private Object memento;

    private ServiceRegistration mbeanProviderService;

    /**
     * Constructs an {@link MBeanProvider} that opens a connection to a
     * {@link MBeanServerConnection}
     *
     * @param jmxServiceDescriptor
     *            the {@link JMXServiceDescriptor} which was used to make the
     *            connection to the JMX end point
     * @param connector
     *            the {@link JMXConnector} that represents the JMX connection
     * @throws IOException
     *             if the connection to the MBean Server cannot be made
     */
    public KarafMBeanProvider(final JMXServiceDescriptor jmxServiceDescriptor, final JMXConnector connector) throws IOException {
        this.connector = connector;
        this.jmxServiceDescriptor = jmxServiceDescriptor;
        this.mbeanServer = connector.getMBeanServerConnection();

        this.connectionHandler = Executors.newSingleThreadExecutor();

        this.connector.addConnectionNotificationListener(this, null, null);
    }

    @Override
    public void close() {

        // JMXConnector.close() is a potentially long running operation.
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    connector.removeConnectionNotificationListener(KarafMBeanProvider.this);
                    connector.close();
                } catch (final IOException e) {
                    // Do nothing
                    // KarafWorkbenchActivator.getLogger().info("Unable to close connection to JMX MBeanServer", e);
                } catch (final ListenerNotFoundException e) {
                    // Nothing to do since this is impossible
                }
            }
        };

        connectionHandler.submit(r);

        if (isOpen()) {
            unregisterServices();
        }
    }

    @Override
    public JMXServiceDescriptor getJMXServiceDescriptor() {
        return jmxServiceDescriptor;
    }

    @Override
    public <T> T getMBean(final ObjectName objectName, final Class<T> interfaceClass) {
        return MBeanServerInvocationHandler.newProxyInstance(
                mbeanServer,
                objectName,
                interfaceClass,
                false);
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection() {
        return mbeanServer;
    }

    @Override
    public void handleNotification(final Notification notification, final Object handback) {
    }

    /**
     * Determines if this provider is open by examining if a memento has been
     * set.
     *
     * @return true if the memento has been set, false otherwise
     */
    @Override
    public boolean isOpen() {
        return memento != null;
    }

    @Override
    public void open(final Object memento) {
        if (memento == null) {
            throw new NullPointerException("Cannot specify a null memento");
        }

        if (isOpen()) {
            // Log something at Trace level?
            return;
        }

        this.memento = memento;

        registerServices();
    }

    private void registerServices() {
        final BundleContext bundleContext =
            KarafWorkbenchActivator.getDefault().getBundle().getBundleContext();

        final Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
        dictionary.put(MBeanProvider.KARAF_WORKBENCH_SERVICES_ID, memento);

        mbeanProviderService = bundleContext.registerService(MBeanProvider.class.getName(), this, dictionary);
    }

    private void unregisterServices() {
        mbeanProviderService.unregister();
        mbeanProviderService = null;
    }

}
