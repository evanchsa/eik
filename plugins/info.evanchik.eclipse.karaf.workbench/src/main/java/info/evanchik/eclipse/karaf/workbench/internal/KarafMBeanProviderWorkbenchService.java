/**
 * Copyright (c) 2010 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.MBeanProvider;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
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
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.SocketUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafMBeanProviderWorkbenchService implements KarafWorkbenchService {

    public static final String JMX_DOMAIN_SERVICE_KEY = "jmxDomain"; //$NON-NLS-1$

    public static final String JMX_JMXRMI_DOMAIN = "jmxrmi"; //$NON-NLS-1$

    private final Map<String, KarafMBeanProvider> mbeanProviderMap =
        Collections.synchronizedMap(new HashMap<String, KarafMBeanProvider>());

    private final Map<String, MBeanServerConnectionJob> mbeanConnectionJobMap =
        Collections.synchronizedMap(new HashMap<String, MBeanServerConnectionJob>());

    private final Map<String, RuntimeDataProvider> runtimeDataProviderMap =
        Collections.synchronizedMap(new HashMap<String, RuntimeDataProvider>());

    private final Map<String, ServiceRegistration> serviceRegistrationMap =
        Collections.synchronizedMap(new HashMap<String, ServiceRegistration>());

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(final KarafWorkingPlatformModel platformModel) {
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
        final List<String> arguments = new ArrayList<String>();

        /*
         * Ensure the Remote JMX connector port is unique
         */
        final int jmxPort = SocketUtil.findFreePort();

        if (jmxPort == -1) {
            throw new CoreException(new Status(IStatus.ERROR, KarafWorkbenchActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX connection"));
        }

        arguments.add("-Dcom.sun.management.jmxremote.authenticate=false"); //$NON-NLS-1$
        arguments.add("-Dcom.sun.management.jmxremote.port=" + new Integer(jmxPort).toString()); //$NON-NLS-1$
        arguments.add("-Dcom.sun.management.jmxremote.ssl=false"); //$NON-NLS-1$

        final MBeanServerConnectionJob mbeanConnectionJob;

        final String memento = configuration.getMemento();

        try {
            final JMXServiceURL standardJmxConnection = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi"); //$NON-NLS-1$ $NON-NLS-2$

            final JMXServiceDescriptor descriptor = new JMXServiceDescriptor(
                        configuration.getName(),
                        standardJmxConnection,
                        null,
                        null,
                        "jmxrmi");

            mbeanConnectionJob = new MBeanServerConnectionJob(configuration.getName(), descriptor);

            mbeanConnectionJobMap.put(memento, mbeanConnectionJob);

        } catch(final MalformedURLException e) {
            KarafWorkbenchActivator.getLogger().error("Unable to connect to JMX endpoint on Karaf instance", e);

            throw new CoreException(new Status(IStatus.ERROR, "", ""));
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
                    mbeanProvider = new KarafMBeanProvider(mbeanConnectionJob.getJmxClient());
                    mbeanProvider.open(memento);

                    mbeanProviderMap.put(memento, mbeanProvider);
                } catch (final IOException e) {
                    KarafWorkbenchActivator.getLogger().error("Unable to create MBeanProvider from JMXConnector", e);

                    return;
                }

                final KarafRuntimeDataProvider runtimeDataProvider =
                    createRuntimeDataProvider(configuration, memento, mbeanProvider);

                registerRuntimeDataProviderService(memento, runtimeDataProvider);
            }

            /**
             * @param configuration
             * @param memento
             * @param mbeanProvider
             * @return
             */
            private KarafRuntimeDataProvider createRuntimeDataProvider(
                    final ILaunchConfiguration configuration,
                    final String memento, final KarafMBeanProvider mbeanProvider) {
                final KarafRuntimeDataProvider runtimeDataProvider = new KarafRuntimeDataProvider(configuration.getName(), mbeanProvider);
                runtimeDataProvider.start();

                runtimeDataProviderMap.put(memento, runtimeDataProvider);
                return runtimeDataProvider;
            }

            /**
             * @param memento
             * @param runtimeDataProvider
             */
            private void registerRuntimeDataProviderService(
                    final String memento,
                    final KarafRuntimeDataProvider runtimeDataProvider) {
                /*
                 * Registers services against the running Karaf instance so that the Eclipse
                 * workbench can control and retrieve information from the process.
                 */
                final BundleContext bundleContext =
                    KarafWorkbenchActivator.getDefault().getBundle().getBundleContext();

                final Dictionary<String, Object> dictionary =
                    new Hashtable<String, Object>();

                final ServiceRegistration runtimeDataProviderServiceReg =
                    bundleContext.registerService(
                            RuntimeDataProvider.class.getName(),
                            runtimeDataProvider,
                            dictionary);

                serviceRegistrationMap.put(memento, runtimeDataProviderServiceReg);
            }

        };

        mbeanConnectionJob.addJobChangeListener(listener);
        mbeanConnectionJob.schedule(MBeanServerConnectionJob.DEFAULT_INITIAL_SCHEDULE_DELAY);

        return arguments;
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
                        final MBeanServerConnectionJob job = mbeanConnectionJobMap.get(memento);
                        if (job != null) {
                            job.cancel();
                        }

                        final ServiceRegistration serviceRegistration = serviceRegistrationMap.get(memento);
                        if (serviceRegistration != null) {
                            serviceRegistration.unregister();
                        }

                        final RuntimeDataProvider runtimeDataProvider = runtimeDataProviderMap.get(memento);
                        if (runtimeDataProvider != null) {
                            runtimeDataProvider.stop();
                        }

                        final MBeanProvider mbeanProvider = mbeanProviderMap.get(memento);
                        if (mbeanProvider != null) {
                            mbeanProvider.close();
                        }
                    }
                }
            }
        };
    }
}
