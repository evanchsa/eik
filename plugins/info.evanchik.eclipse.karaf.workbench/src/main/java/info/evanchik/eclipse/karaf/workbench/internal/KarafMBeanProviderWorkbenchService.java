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

    private final Map<String, KarafRuntimeDataProviderItem> runtimeDataProviderMap =
        new HashMap<String, KarafRuntimeDataProviderItem>();

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private static final class KarafRuntimeDataProviderItem {

        /**
         * Provides access to the "standard" MBeans found in a Karaf server
         */
        private final KarafMBeanProvider mbeanProvider;

        private final MBeanServerConnectionJob mbeanConnectionJob;

        private final RuntimeDataProvider runtimeDataProvider;

        private final ServiceRegistration runtimeDataProviderServiceRegistration;

        /**
         *
         * @param mbeanProvider
         * @param mbeanServerConnectionJob
         * @param runtimeDataProvider
         * @param runtimeDataProviderServiceRegistration
         */
        public KarafRuntimeDataProviderItem(
                KarafMBeanProvider mbeanProvider,
                MBeanServerConnectionJob mbeanServerConnectionJob,
                RuntimeDataProvider runtimeDataProvider,
                ServiceRegistration runtimeDataProviderServiceRegistration)
        {
            this.mbeanProvider = mbeanProvider;
            this.mbeanConnectionJob = mbeanServerConnectionJob;
            this.runtimeDataProvider = runtimeDataProvider;
            this.runtimeDataProviderServiceRegistration = runtimeDataProviderServiceRegistration;
        }

        public KarafMBeanProvider getMbeanProvider() {
            return mbeanProvider;
        }

        public MBeanServerConnectionJob getMbeanConnectionJob() {
            return mbeanConnectionJob;
        }

        public RuntimeDataProvider getRuntimeDataProvider() {
            return runtimeDataProvider;
        }

        public ServiceRegistration getRuntimeDataProviderServiceRegistration() {
            return runtimeDataProviderServiceRegistration;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mbeanConnectionJob == null) ? 0 : mbeanConnectionJob.hashCode());
            result = prime * result + ((mbeanProvider == null) ? 0 : mbeanProvider.hashCode());
            result = prime * result + ((runtimeDataProvider == null) ? 0 : runtimeDataProvider.hashCode());
            result = prime * result + ((runtimeDataProviderServiceRegistration == null) ? 0 : runtimeDataProviderServiceRegistration.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            final KarafRuntimeDataProviderItem other = (KarafRuntimeDataProviderItem) obj;
            if (mbeanConnectionJob == null) {
                if (other.mbeanConnectionJob != null)
                    return false;
            } else if (!mbeanConnectionJob.equals(other.mbeanConnectionJob)) {
                return false;
            }

            if (mbeanProvider == null) {
                if (other.mbeanProvider != null)
                    return false;
            } else if (!mbeanProvider.equals(other.mbeanProvider)) {
                return false;
            }

            if (runtimeDataProvider == null) {
                if (other.runtimeDataProvider != null) {
                    return false;
                }
            } else if (!runtimeDataProvider.equals(other.runtimeDataProvider)) {
                return false;
            }

            return true;
        }
    };

    @Override
    public List<BundleEntry> getAdditionalBundles(KarafWorkingPlatformModel platformModel) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(KarafWorkingPlatformModel platformModel) {
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
        } catch(MalformedURLException e) {
            KarafWorkbenchActivator.getLogger().error("Unable to connect to JMX endpoint on Karaf instance", e);

            throw new CoreException(new Status(IStatus.ERROR, "", ""));
        }

        final IJobChangeListener listener = new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
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
                } catch (IOException e) {
                    KarafWorkbenchActivator.getLogger().error("Unable to create MBeanProvider from JMXConnector", e);

                    return;
                }

                final KarafRuntimeDataProvider runtimeDataProvider = new KarafRuntimeDataProvider(configuration.getName(), mbeanProvider);
                runtimeDataProvider.start();

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

                final KarafRuntimeDataProviderItem item =
                    new KarafRuntimeDataProviderItem(
                            mbeanProvider,
                            mbeanConnectionJob,
                            runtimeDataProvider,
                            runtimeDataProviderServiceReg);

                runtimeDataProviderMap.put(memento, item);
            }

        };

        mbeanConnectionJob.addJobChangeListener(listener);
        mbeanConnectionJob.schedule(MBeanServerConnectionJob.DEFAULT_INITIAL_SCHEDULE_DELAY);

        return arguments;
    }

    @Override
    public void launch(
            KarafWorkingPlatformModel platformModel,
            ILaunchConfiguration configuration,
            String mode,
            ILaunch launch,
            IProgressMonitor monitor) throws CoreException
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

                for (DebugEvent event : events) {
                    if (   process != null
                        && process.equals(event.getSource())
                        && event.getKind() == DebugEvent.TERMINATE)
                    {
                        final KarafRuntimeDataProviderItem item =
                            runtimeDataProviderMap.get(memento);

                        if (item == null) {
                            return;
                        }

                        item.getMbeanConnectionJob().cancel();
                        item.getRuntimeDataProviderServiceRegistration().unregister();
                        item.getRuntimeDataProvider().stop();
                        item.getMbeanProvider().close();

                        runtimeDataProviderMap.remove(memento);
                    }
                }
            }
        };
    }
}
