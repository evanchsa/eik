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

import info.evanchik.eclipse.karaf.core.IKarafConstants;
import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
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
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.SocketUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafWorkbenchService implements KarafWorkbenchService {

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyList();
        }

        final String[] bundles = {
                "info.evanchik.karaf.app", //$NON-NLS-1$
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

        for (final String b : bundles) {

            // If the bundle is already present in the platform, don't add it
            if (platformModel.getState().getBundle(b, null) != null) {
                continue;
            }

            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(b);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(final KarafWorkingPlatformModel platformModel) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyMap();
        }

        final Map<String, String> equinoxProperties = new HashMap<String, String>();

        // TODO: Need to interpolate the config.properties file
        final Properties currentConfig;
        try {
            currentConfig =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getConfigurationDirectory().toFile(),
                    IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE,
                    true);

            /*
             * Populate the config.ini with all of the typical Karaf properties that
             * are not found in the System properties
             */
            for (final Map.Entry<Object, Object> e : currentConfig.entrySet()) {
                equinoxProperties.put((String)e.getKey(), (String)e.getValue());
            }

        } catch(final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to load configuration file: " + platformModel.getConfigurationDirectory(), e);
        }

        equinoxProperties.put(
                IKarafConstants.KARAF_BASE_PROP,
                platformModel.getParentKarafModel().getRootDirectory().toOSString());

        equinoxProperties.put(
                IKarafConstants.KARAF_HOME_PROP,
                platformModel.getParentKarafModel().getRootDirectory().toOSString());

        /*
         * Adds the $TARGET_HOME/<system plugins> directory to the default
         * bundle.locations search space
         */
        equinoxProperties.put(
                IKarafConstants.KARAF_BUNDLE_LOCATIONS_PROP,
                platformModel.getPluginRootDirectory().toOSString());

        return equinoxProperties;
    }

    @Override
    public List<String> getVMArguments(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration) throws CoreException
    {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyList();
        }

        final List<String> arguments = new ArrayList<String>();

        arguments.add(
            KarafCorePluginUtils.constructSystemProperty(
                IKarafConstants.KARAF_BASE_PROP,
                platformModel.getParentKarafModel().getRootDirectory().toString()));

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        IKarafConstants.KARAF_HOME_PROP,
                        platformModel.getParentKarafModel().getRootDirectory().toString()));

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "java.util.logging.config.file", //$NON-NLS-1$
                        platformModel.getParentKarafModel().getConfigurationDirectory().append("java.util.logging.properties").toString())); //$NON-NLS-1$

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        IKarafConstants.KARAF_DATA_PROP,
                        platformModel.getRootDirectory().append("data").toString())); //$NON-NLS-1$

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        IKarafConstants.KARAF_INSTANCES_PROP,
                        platformModel.getRootDirectory().append("instances").toString())); //$NON-NLS-1$

        final Boolean startLocalConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE,
                    true);
        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "karaf.startLocalConsole", //$NON-NLS-1$
                        startLocalConsole.toString()));

        final Boolean startRemoteConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                    false);
        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "karaf.startRemoteShell", //$NON-NLS-1$
                        startRemoteConsole.toString()));

        return arguments;
    }

    @Override
    public void initialize(final KarafWorkingPlatformModel platformModel, final ILaunchConfigurationWorkingCopy configuration) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return;
        }
    }

    /**
     * @param platformModel
     * @throws CoreException
     */
    private void configureJMXConnector(
            final KarafWorkingPlatformModel platformModel) throws CoreException {
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
    }

    @Override
    public void launch(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration,
            final String mode,
            final ILaunch launch,
            final IProgressMonitor monitor) throws CoreException
    {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return;
        }

        configureJMXConnector(platformModel);
    }
}
