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

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.jmx.KarafJMXPlugin;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
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
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jdt.launching.SocketUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class JMXWorkbenchService implements KarafWorkbenchService {

    private final Map<String, JMXServiceDescriptor> jmxServiceDescriptorMap =
        new HashMap<String, JMXServiceDescriptor>();

    @Override
    public List<BundleEntry> getAdditionalBundles(KarafWorkingPlatformModel platformModel) {
        String[] jmxBundles = {
                KarafJMXPlugin.PLUGIN_ID,
                "org.eclipse.core.contenttype",
                "org.eclipse.core.jobs",
                "org.eclipse.core.runtime",
                "org.eclipse.core.runtime.compatibility.auth",
                "org.eclipse.equinox.app",
                "org.eclipse.equinox.common",
                "org.eclipse.equinox.registry",
                "org.eclipse.equinox.preferences",
                "org.eclipse.osgi.util"
        };

        final List<BundleEntry> bundleEntries = new ArrayList<BundleEntry>();

        for (String jmxBundle : jmxBundles) {
            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(jmxBundle);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(KarafWorkingPlatformModel platformModel) {
        return Collections.emptyMap();
    }

    @Override
    public List<String> getVMArguments(KarafWorkingPlatformModel platformModel, ILaunchConfiguration configuration)
            throws CoreException
    {
        final List<String> arguments = new ArrayList<String>();


        /*
         * Establish the JMX connector port for the JMX service "jmxserver"
         */
        int jmxServicePort = SocketUtil.findFreePort();

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

        } catch (MalformedURLException e) {
            // TODO: Throw a CoreException
        }

        arguments.add("-Dorg.eclipse.equinox.jmx.server.port=" + new Integer(jmxServicePort).toString()); //$NON-NLS-1$

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
        final String memento = configuration.getMemento();

        final JMXServiceDescriptor jmxServiceDescriptor =
            jmxServiceDescriptorMap.get(memento);

        KarafWorkbenchActivator.getDefault().getJMXServiceManager().addJMXService(jmxServiceDescriptor);

        final ILaunchListener launchListener = getLaunchListener(launch);
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
    }

    /**
     *
     * @param launch
     * @return
     * @throws CoreException
     */
    private ILaunchListener getLaunchListener(final ILaunch launch) throws CoreException {

        final String memento = launch.getLaunchConfiguration().getMemento();

        return new ILaunchListener() {

            @Override
            public void launchAdded(ILaunch l) {
            }

            @Override
            public void launchChanged(ILaunch l) {
            }

            @Override
            public void launchRemoved(ILaunch l) {
                if (!l.equals(launch)) {
                    return;
                }

                final JMXServiceDescriptor jmxServiceDescriptor =
                    jmxServiceDescriptorMap.get(memento);

                KarafWorkbenchActivator.getDefault().getJMXServiceManager().removeJMXService(jmxServiceDescriptor);

                jmxServiceDescriptorMap.remove(memento);
            }

        };
    }
}
