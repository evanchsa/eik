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
package info.evanchik.eclipse.karaf.ui.workbench.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.ManagementSection;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationConstants;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.SocketUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafWorkbenchService implements KarafWorkbenchService {

    public List<BundleEntry> getAdditionalBundles(KarafWorkingPlatformModel platformModel) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyList();
        }
        String[] bundles = {
                "info.evanchik.karaf.app",
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

        for (String b : bundles) {
            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(b);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    public Map<String, String> getAdditionalEquinoxConfiguration(KarafWorkingPlatformModel platformModel) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyMap();
        }

        final Map<String, String> equinoxProperties = new HashMap<String, String>();

        final Properties currentConfig;
        try {
            currentConfig =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getConfigurationDirectory().toFile(),
                    KarafPlatformModel.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE);

            /*
             * Populate the config.ini with all of the typical Karaf properties that
             * are not found in the System properties
             */
            for (Map.Entry<Object, Object> e : currentConfig.entrySet()) {
                equinoxProperties.put((String)e.getKey(), (String)e.getValue());

            }
        } catch(CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to load configuration file: " + platformModel.getConfigurationDirectory(), e);
        }

        equinoxProperties.put(
                KarafPlatformModel.KARAF_BASE_PROP,
                platformModel.getRootDirectory().toOSString());

        equinoxProperties.put(
                KarafPlatformModel.KARAF_HOME_PROP,
                platformModel.getRootDirectory().toOSString());

        /*
         * Adds the $TARGET_HOME/<system plugins> directory to the default
         * bundle.locations search space
         */
        equinoxProperties.put(
                KarafPlatformModel.KARAF_BUNDLE_LOCATIONS_PROP,
                platformModel.getPluginRootDirectory().toOSString());

        return equinoxProperties;
    }

    public List<String> getVMArguments(
            KarafWorkingPlatformModel platformModel,
            ILaunchConfiguration configuration) throws CoreException
    {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyList();
        }

        final List<String> arguments = new ArrayList<String>();

        arguments.add("-D" + KarafPlatformModel.KARAF_BASE_PROP + "=" + platformModel.getRootDirectory()); //$NON-NLS-1$ $NON-NLS-2$
        arguments.add("-D" + KarafPlatformModel.KARAF_HOME_PROP + "=" + platformModel.getRootDirectory()); //$NON-NLS-1$ $NON-NLS-2$

        /*
         * Ensure that the RMI registry port for the JMX connector is unique
         */
        final int jmxRegistryPort = SocketUtil.findFreePort();

        if (jmxRegistryPort == -1) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX RMI Registry"));
        }

        final ManagementSection managementSection =
            (ManagementSection) Platform.getAdapterManager().getAdapter(
                    platformModel,
                    ManagementSection.class
        );

        managementSection.load();
        managementSection.setPort(jmxRegistryPort);
        managementSection.save();

        final Boolean startLocalConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE,
                    true);

        final Boolean startRemoteConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                    false);

        arguments.add("-Djava.util.logging.config.file=" + platformModel.getConfigurationDirectory() + "/java.util.logging.properties");
        arguments.add("-Dkaraf.startLocalConsole=" + startLocalConsole.toString()); //$NON-NLS-1$
        arguments.add("-Dkaraf.startRemoteShell=" + startRemoteConsole.toString()); //$NON-NLS-1$

        return arguments;
    }

    public void launch(
            KarafWorkingPlatformModel platformModel,
            ILaunchConfiguration configuration,
            String mode,
            ILaunch launch,
            IProgressMonitor monitor) throws CoreException
    {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return;
        }

        // Do nothing
    }
}
