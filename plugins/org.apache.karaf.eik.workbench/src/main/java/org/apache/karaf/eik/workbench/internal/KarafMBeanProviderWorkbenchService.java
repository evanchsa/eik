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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.core.equinox.BundleEntry;
import org.apache.karaf.eik.ui.workbench.KarafWorkbenchService;
import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.MBeanProvider;
import org.apache.karaf.eik.workbench.WorkbenchServiceManager;
import org.apache.karaf.eik.workbench.jmx.JMXServiceDescriptor;
import org.apache.karaf.eik.workbench.jmx.LocalJMXServiceDescriptor;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.SocketUtil;

public class KarafMBeanProviderWorkbenchService implements KarafWorkbenchService {

    private final class JMXServiceCleanupLaunchListener implements ILaunchListener {

        @Override
        public void launchAdded(final ILaunch launch) {
        }

        @Override
        public void launchChanged(final ILaunch launch) {
        }

        @Override
        public void launchRemoved(final ILaunch launch) {
            try {
                final String memento = launch.getLaunchConfiguration().getMemento();

                final JMXServiceDescriptor jmxServiceDescriptor = mbeanProviderDataMap.get(memento).getJmxServiceDescriptor();

                if (jmxServiceDescriptor == null) {
                    return;
                }

                jmxServiceManager.remove(jmxServiceDescriptor);

                mbeanProviderDataMap.remove(memento);
            } catch (final CoreException e) {
                // Log something
            }
        }
    };

    private static final class KarafMBeanProviderEntry {

        private JMXServiceDescriptor jmxServiceDescriptor;

        private MBeanProvider mbeanProvider;

        private MBeanServerConnectionJob mbeanServerConnectionJob;

        private RuntimeDataProvider runtimeDataProvider;

        public JMXServiceDescriptor getJmxServiceDescriptor() {
            return jmxServiceDescriptor;
        }

        public MBeanProvider getMbeanProvider() {
            return mbeanProvider;
        }

        public MBeanServerConnectionJob getMBeanServerConnectionJob() {
            return mbeanServerConnectionJob;
        }

        public RuntimeDataProvider getRuntimeDataProvider() {
            return runtimeDataProvider;
        }

        public void setJmxServiceDescriptor(
                final JMXServiceDescriptor jmxServiceDescriptor) {
            this.jmxServiceDescriptor = jmxServiceDescriptor;
        }

        public void setMbeanConnectionJob(final MBeanServerConnectionJob mbeanServerConnectionJob) {
            this.mbeanServerConnectionJob = mbeanServerConnectionJob;
        }

        public void setMbeanProvider(final MBeanProvider mbeanProvider) {
            this.mbeanProvider = mbeanProvider;
        }

        public void setRuntimeDataProvider(final RuntimeDataProvider runtimeDataProvider) {
            this.runtimeDataProvider = runtimeDataProvider;
        }
    };

    public static final String JMX_JMXRMI_DOMAIN = "jmxrmi"; //$NON-NLS-1$

    private WorkbenchServiceManager<JMXServiceDescriptor> jmxServiceManager;

    private final Map<String, KarafMBeanProviderEntry> mbeanProviderDataMap =
        Collections.synchronizedMap(new HashMap<String, KarafMBeanProviderEntry>());

    private WorkbenchServiceManager<MBeanProvider> mbeanProviderManager;

    private WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager;

    public KarafMBeanProviderWorkbenchService() {
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new JMXServiceCleanupLaunchListener());

        jmxServiceManager = KarafWorkbenchActivator.getDefault().getJMXServiceManager();
        mbeanProviderManager = KarafWorkbenchActivator.getDefault().getMBeanProviderManager();
        runtimeDataProviderManager = KarafWorkbenchActivator.getDefault().getRuntimeDataProviderManager();
    }

    @Override
    public List<BundleEntry> getAdditionalBundles(
        final KarafWorkingPlatformModel platformModel,
        final ILaunchConfiguration configuration)
    {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(
        final KarafWorkingPlatformModel platformModel,
        final ILaunchConfiguration configuration)
    {
        return Collections.emptyMap();
    }

    /**
     * Starts a background system job that will connect to the Karaf MBeanServer
     *
     * 1. Register a remote JMX port to bind the JMX server to
     * 2. Establish an MBeanServerConnectionJob to connect to that port
     * 3. When connected: Register OSGi C&C MBeanProvider in the service registry
     * 4. When connected: Register the RuntimeDataProvider in the service registry
     */
    @Override
    public List<String> getVMArguments(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration) throws CoreException
    {
        final String memento = configuration.getMemento();
        mbeanProviderDataMap.put(memento, new KarafMBeanProviderEntry());

        final List<String> arguments = new ArrayList<String>();

        /*
         * Ensure the Remote JMX connector port is unique
         */
        final int jmxPort = SocketUtil.findFreePort();

        if (jmxPort == -1) {
            throw new CoreException(new Status(IStatus.ERROR, KarafWorkbenchActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX connection"));
        }

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "com.sun.management.jmxremote.authenticate", //$NON-NLS-1$
                        "false")); //$NON-NLS-1$
        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "com.sun.management.jmxremote.ssl", //$NON-NLS-1$
                        "false")); //$NON-NLS-1$
        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "com.sun.management.jmxremote.port", //$NON-NLS-1$
                        new Integer(jmxPort).toString()));

        final MBeanServerConnectionJob mbeanConnectionJob;
        try {
            final JMXServiceURL standardJmxConnection = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi"); //$NON-NLS-1$ $NON-NLS-2$

            final JMXServiceDescriptor descriptor = new LocalJMXServiceDescriptor(
                        configuration.getName(),
                        platformModel,
                        standardJmxConnection,
                        null,
                        null,
                        JMX_JMXRMI_DOMAIN);

            mbeanConnectionJob = new MBeanServerConnectionJob(configuration.getName(), descriptor);

            mbeanProviderDataMap.get(memento).setMbeanConnectionJob(mbeanConnectionJob);
            mbeanProviderDataMap.get(memento).setJmxServiceDescriptor(descriptor);

            jmxServiceManager.add(descriptor);
        } catch(final MalformedURLException e) {
            KarafWorkbenchActivator.getLogger().error("Unable to connect to JMX endpoint on Karaf instance", e);

            throw new CoreException(new Status(IStatus.ERROR, "", "")); //$NON-NLS-1$ $NON-NLS-2$
        }

        final IJobChangeListener listener = new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
                final IStatus result = event.getResult();
                if (result == null || !result.isOK()) {
                    // TODO: Log something
                    return;
                }

                if (!mbeanConnectionJob.isConnected()) {
                    // TODO: Log something
                    return;
                }

                final KarafMBeanProvider mbeanProvider;
                try {
                    final JMXServiceDescriptor jmxServiceDescriptor = mbeanProviderDataMap.get(memento).getJmxServiceDescriptor();
                    mbeanProvider = new LocalKarafMBeanProvider(jmxServiceDescriptor, mbeanConnectionJob.getJmxClient(), platformModel);
                    mbeanProvider.open(memento);

                    mbeanProviderDataMap.get(memento).setMbeanProvider(mbeanProvider);
                    mbeanProviderManager.add(mbeanProvider);
                } catch (final IOException e) {
                    KarafWorkbenchActivator.getLogger().error("Unable to create MBeanProvider from JMXConnector", e);

                    return;
                }

                final KarafRuntimeDataProvider runtimeDataProvider = new KarafRuntimeDataProvider(configuration.getName(), mbeanProvider);
                runtimeDataProvider.start();

                mbeanProviderDataMap.get(memento).setRuntimeDataProvider(runtimeDataProvider);
                runtimeDataProviderManager.add(runtimeDataProvider);
            }
        };

        mbeanConnectionJob.addJobChangeListener(listener);
        mbeanConnectionJob.schedule(MBeanServerConnectionJob.DEFAULT_INITIAL_SCHEDULE_DELAY);

        return arguments;
    }

    @Override
    public void initialize(final KarafWorkingPlatformModel platformModel,
            final ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void launch(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration,
            final String mode,
            final ILaunch launch,
            final IProgressMonitor monitor) throws CoreException
    {
        final IDebugEventSetListener debugListener = getDebugEventListener(launch);
        DebugPlugin.getDefault().addDebugEventListener(debugListener);
    }

    public void setJmxServiceManager(final WorkbenchServiceManager<JMXServiceDescriptor> jmxServiceManager) {
        this.jmxServiceManager = jmxServiceManager;
    }

    public void setMbeanProviderManager(final WorkbenchServiceManager<MBeanProvider> mbeanProviderManager) {
        this.mbeanProviderManager = mbeanProviderManager;
    }

    public void setRuntimeDataProviderManager(final WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager) {
        this.runtimeDataProviderManager = runtimeDataProviderManager;
    }

    /**
     * Registers an event listener on the debug session that responds to
     * {@link DebugEvent.TERMINATE} events. This will stop the MBBean connection
     * job, the {@link RuntimeDataProvider} and the {@link MBeanProvider}<br>
     * <br>
     * This will also cleanup the services registered during this debug session.
     *
     * @param launch
     *            the launch process
     */
    private IDebugEventSetListener getDebugEventListener(final ILaunch launch) throws CoreException {
        final IProcess process = launch.getProcesses()[0];

        final String memento =
            launch.getLaunchConfiguration().getMemento();

        return new IDebugEventSetListener() {
            @Override
            public void handleDebugEvents(final DebugEvent[] events) {
                if (events == null) {
                    return;
                }

                for (final DebugEvent event : events) {
                    if (   process != null
                        && process.equals(event.getSource())
                        && event.getKind() == DebugEvent.TERMINATE)
                    {
                        final JMXServiceDescriptor descriptor = mbeanProviderDataMap.get(memento).getJmxServiceDescriptor();
                        jmxServiceManager.remove(descriptor);

                        final MBeanServerConnectionJob job = mbeanProviderDataMap.get(memento).getMBeanServerConnectionJob();
                        if (job != null) {
                            job.cancel();
                        }

                        final RuntimeDataProvider runtimeDataProvider = mbeanProviderDataMap.get(memento).getRuntimeDataProvider();
                        runtimeDataProviderManager.remove(runtimeDataProvider);
                        if (runtimeDataProvider != null) {
                            runtimeDataProvider.stop();
                        }

                        final MBeanProvider mbeanProvider = mbeanProviderDataMap.get(memento).getMbeanProvider();
                        mbeanProviderManager.remove(mbeanProvider);

                        if (mbeanProvider != null) {
                            mbeanProvider.close();
                        }
                    }
                }
            }
        };
    }

}
