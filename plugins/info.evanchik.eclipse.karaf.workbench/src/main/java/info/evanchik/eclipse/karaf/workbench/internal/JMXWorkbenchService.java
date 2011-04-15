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
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jdt.launching.SocketUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class JMXWorkbenchService implements KarafWorkbenchService {

    private final Map<String, JMXServiceDescriptor> jmxServiceDescriptorMap =
        Collections.synchronizedMap(new HashMap<String, JMXServiceDescriptor>());

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel) {
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
    public Map<String, String> getAdditionalEquinoxConfiguration(final KarafWorkingPlatformModel platformModel) {
        return Collections.emptyMap();
    }

    @Override
    public List<String> getVMArguments(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration)
            throws CoreException
    {
        final List<String> arguments = new ArrayList<String>();

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

        } catch (final MalformedURLException e) {
            // TODO: Throw a CoreException
        }

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
            public void launchAdded(final ILaunch l) {
            }

            @Override
            public void launchChanged(final ILaunch l) {
            }

            @Override
            public void launchRemoved(final ILaunch l) {
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
