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
package info.evanchik.eclipse.karaf.workbench.jmx.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.jmx.KarafJMXPlugin;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jdt.launching.SocketUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class JMXWorkbenchService implements KarafWorkbenchService {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
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

                final JMXServiceDescriptor jmxServiceDescriptor =
                    jmxServiceDescriptorMap.get(memento);

                if (jmxServiceDescriptor == null) {
                    return;
                }

                jmxServiceManager.removeJMXService(jmxServiceDescriptor);

                jmxServiceDescriptorMap.remove(memento);
            } catch (final CoreException e) {
                // Log something
            }
        }
    };

    private IJMXServiceManager jmxServiceManager;

    private final Map<String, JMXServiceDescriptor> jmxServiceDescriptorMap =
        Collections.synchronizedMap(new HashMap<String, JMXServiceDescriptor>());

    public JMXWorkbenchService() {
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new JMXServiceCleanupLaunchListener());

        jmxServiceManager = KarafWorkbenchActivator.getDefault().getJMXServiceManager();
    }

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) {
        final String[] jmxBundles = {
                KarafJMXPlugin.PLUGIN_ID,
                "org.eclipse.core.contenttype", //$NON-NLS-1$
                "org.eclipse.core.jobs", //$NON-NLS-1$
                "org.eclipse.core.runtime", //$NON-NLS-1$
                "org.eclipse.core.runtime.compatibility.auth", //$NON-NLS-1$
                "org.eclipse.equinox.app", //$NON-NLS-1$
                "org.eclipse.equinox.common", //$NON-NLS-1$
                "org.eclipse.equinox.registry", //$NON-NLS-1$
                "org.eclipse.equinox.preferences", //$NON-NLS-1$
                "org.eclipse.osgi.util" //$NON-NLS-1$
        };

        final List<BundleEntry> bundleEntries = new ArrayList<BundleEntry>();

        for (final String jmxBundle : jmxBundles) {
            // If the bundle is already present in the platform, don't add it
            if (platformModel.getState().getBundle(jmxBundle, null) != null) {
                continue;
            }

            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(jmxBundle);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration)
    {
        return Collections.emptyMap();
    }

    @Override
    public List<String> getVMArguments(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration)
        throws CoreException
    {
        return Collections.emptyList();
    }

    @Override
    public void initialize(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfigurationWorkingCopy configuration)
    {
    }

    @Override
    public void launch(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration,
            final String mode,
            final ILaunch launch,
            final IProgressMonitor monitor)
        throws CoreException
    {
        configureJMXServiceDescriptor(configuration);
    }

    /**
     * Registers a {@link JMXServiceDescriptor} for the Karaf instance
     *
     * @param configuration
     *            the launch configuration
     * @throws CoreException
     *             thrown if there is a problem accessing any of the launch
     *             configuration's data
     */
    private void configureJMXServiceDescriptor(
            final ILaunchConfiguration configuration) throws CoreException {
        /*
         * Establish the JMX connector port for the JMX service "jmxserver"
         */
        final int jmxServicePort = SocketUtil.findFreePort();

        if (jmxServicePort == -1) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    KarafWorkbenchActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for Karaf JMX Services connection"));
        }

        try {

            final JMXServiceURL jmxServiceConnection = new JMXServiceURL(
                    JMXServiceDescriptor.DEFAULT_PROTOCOL,
                    "localhost",
                    jmxServicePort,
                    "/" + JMXServiceDescriptor.DEFAULT_DOMAIN); //$NON-NLS-1$

            final JMXServiceDescriptor jmxServiceDescriptor = new JMXServiceDescriptor(
                        configuration.getName(),
                        jmxServiceConnection,
                        null,
                        null,
                        JMXServiceDescriptor.DEFAULT_DOMAIN);

            jmxServiceDescriptorMap.put(configuration.getMemento(), jmxServiceDescriptor);
            jmxServiceManager.addJMXService(jmxServiceDescriptor);
        } catch (final MalformedURLException e) {
            // TODO: Throw a CoreException
        }
    }

    public void setJmxServiceManager(final IJMXServiceManager jmxServiceManager) {
        this.jmxServiceManager = jmxServiceManager;
    }
}
