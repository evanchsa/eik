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
import info.evanchik.eclipse.karaf.core.PropertyUtils;
import info.evanchik.eclipse.karaf.core.configuration.FeaturesSection;
import info.evanchik.eclipse.karaf.core.configuration.ManagementSection;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModel;
import info.evanchik.eclipse.karaf.core.shell.KarafSshShellConnection.KarafSshConnectionUrl;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationConstants;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.console.KarafRemoteConsole;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafWorkbenchService implements KarafWorkbenchService {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class KarafConsoleLaunchListener implements ILaunchListener {

        /**
         * Returns the console for the given {@link IProcess}, or {@code null}
         * if none.
         *
         * @param process
         *            the {@code IProcess} whose console is to be found
         * @return the console for the given process, or {@code null} if none
         */
        public IConsole findConsole(final IProcess process) {
            final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

            for (final IConsole console : manager.getConsoles()) {
                if (console instanceof KarafRemoteConsole) {
                    final KarafRemoteConsole karafConsole = (KarafRemoteConsole) console;
                    if (karafConsole.getProcess().equals(process)) {
                        return karafConsole;
                    }
                }
            }

            return null;
        }

        /**
         * Returns the {@link IDocument} for the {@link IProcess}, or
         * {@code null} if none is available.
         *
         * @param process
         *            the {@code IProcess} whose document is to be retrieved
         * @return the {@code IDocument} for the specified {@code IProcess} or
         *         {@code null} if one could not be found
         */
        public IDocument getConsoleDocument(final IProcess process) {
            final KarafRemoteConsole console = (KarafRemoteConsole) findConsole(process);
            return console != null ? console.getDocument() : null;
        }

        @Override
        public void launchAdded(final ILaunch launch) {
            launchChanged(launch);
        }

        @Override
        public void launchChanged(final ILaunch launch) {
            if (!isKarafLaunch(launch)) {
                return;
            }

            for (final IProcess process : launch.getProcesses()) {
                if (getConsoleDocument(process) != null) {
                    continue;
                }

                if (process.getStreamsProxy() == null) {
                    continue;
                }

                final String encoding = launch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);

                final KarafRemoteConsole remoteConsole = new KarafRemoteConsole(
                        process,
                        new KarafSshConnectionUrl("localhost", 8101, "smx", "smx"),
                        new ConsoleColorProvider(),
                        "Default Name",
                        encoding);

                remoteConsole.setAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS, process);

                ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { remoteConsole });
            }
        }

        @Override
        public void launchRemoved(final ILaunch launch) {
            if (!isKarafLaunch(launch)) {
                return;
            }

            for (final IProcess process : launch.getProcesses()) {
                final IConsole console = findConsole(process);

                if (console != null) {
                    final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
                    manager.removeConsoles(new IConsole[] { console });
                }
            }
        }

        /**
         * Determines whether or not the {@link ILaunch} is from an EIK launch
         *
         * @param launch
         *            the {@code ILaunch} to evaluate
         * @return true if the {@code ILaunch} is an EIK launch
         */
        private boolean isKarafLaunch(final ILaunch launch) {
            try {
                final ILaunchConfiguration configuration = launch.getLaunchConfiguration();
                if (!configuration.getAttributes().containsKey(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE)) {
                    return false;
                }

                return true;
            } catch (final CoreException e) {
                return false;
            }
        }
    }

    public GenericKarafWorkbenchService() {
        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new KarafConsoleLaunchListener());
    }

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) {
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
    public Map<String, String> getAdditionalEquinoxConfiguration(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyMap();
        }

        final Map<String, String> equinoxProperties = new HashMap<String, String>();

        final Properties currentConfig;
        try {
            currentConfig =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getConfigurationDirectory().toFile(),
                    IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE,
                    true);

            final Properties systemProperties = createLaunchSystemProperties(platformModel, configuration);
            currentConfig.putAll(systemProperties);

            PropertyUtils.interpolateVariables(currentConfig, currentConfig);

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
            final ILaunchConfiguration configuration)
        throws CoreException
    {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return Collections.emptyList();
        }

        final Properties systemProperties = createLaunchSystemProperties(platformModel, configuration);

        final List<String> arguments = new ArrayList<String>();
        for (final Map.Entry<Object, Object> e : systemProperties.entrySet()) {
            arguments.add(
                    KarafCorePluginUtils.constructSystemProperty(
                            (String) e.getKey(),
                            (String) e.getValue()));
        }

        return arguments;
    }

    @Override
    public void initialize(final KarafWorkingPlatformModel platformModel, final ILaunchConfigurationWorkingCopy configuration) {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return;
        }

        configuration.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
                platformModel.getParentKarafModel().getRootDirectory().toString());

        final StringBuffer vmArgs = new StringBuffer();
        if (vmArgs.indexOf("-Declipse.application") == -1) { //$NON-NLS-1$
            if (vmArgs.length() > 0) {
                vmArgs.append(" "); //$NON-NLS-1$
            }
            vmArgs.append(" -Declipse.application=info.evanchik.karaf.app.KarafMain"); //$NON-NLS-1$
        }

        try {
            final String currentVMArguments = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");

            if (currentVMArguments.trim().length() > 0) {
                vmArgs.append(" ").append(currentVMArguments);
            }

            configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs.toString());
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to set default VM arguments", e);
        }
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

        configureKarafFeatures(platformModel, configuration);
        configureJMXConnector(platformModel);
    }

    /**
     *
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
            throw new CoreException(new Status(
                    IStatus.ERROR,
                    KarafUIPluginActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX RMI Registry"));
        }

        final ManagementSection managementSection =
            (ManagementSection) platformModel.getAdapter(ManagementSection.class);

        managementSection.load();
        managementSection.setPort(jmxRegistryPort);
        managementSection.save();
    }

    /**
     *
     * @param platformModel
     * @throws CoreException
     */
    private void configureKarafFeatures(
            final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) throws CoreException {

        if (!configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, true)) {
            return;
        }

        final FeaturesSection featuresSection =
            (FeaturesSection) platformModel.getAdapter(FeaturesSection.class);

        featuresSection.load();

        final String bootFeaturesString =
            configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, ""); //$NON-NLS-1$
        final String[] bootFeaturesArray = bootFeaturesString.split(",");

        final List<String> features = new ArrayList<String>();
        Collections.addAll(features, bootFeaturesArray);

        featuresSection.setBootFeatureNames(features);
        featuresSection.save();
    }

    /**
     * @param platformModel
     * @param configuration
     * @return
     * @throws CoreException
     */
    private Properties createLaunchSystemProperties(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration) throws CoreException {
        final Properties systemProperties = loadSystemProperties(platformModel);

        systemProperties.put(
                IKarafConstants.KARAF_BASE_PROP,
                platformModel.getParentKarafModel().getRootDirectory().toString());

        systemProperties.put(
            IKarafConstants.KARAF_HOME_PROP,
            platformModel.getParentKarafModel().getRootDirectory().toString());

        systemProperties.put(
            "java.util.logging.config.file", //$NON-NLS-1$
            platformModel.getParentKarafModel().getConfigurationDirectory().append("java.util.logging.properties").toString()); //$NON-NLS-1$

        systemProperties.put(
            IKarafConstants.KARAF_DATA_PROP,
            platformModel.getParentKarafModel().getRootDirectory().append("data").toString()); //$NON-NLS-1$

        systemProperties.put(
            IKarafConstants.KARAF_INSTANCES_PROP,
            platformModel.getParentKarafModel().getRootDirectory().append("instances").toString()); //$NON-NLS-1$

        final Boolean startLocalConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE,
                    true);
        systemProperties.put(
            "karaf.startLocalConsole", //$NON-NLS-1$
            startLocalConsole.toString());

        final Boolean startRemoteConsole =
            configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                    false);
        systemProperties.put(
            "karaf.startRemoteShell", //$NON-NLS-1$
            startRemoteConsole.toString());

        PropertyUtils.interpolateVariables(systemProperties, systemProperties);
        return systemProperties;
    }

    /**
     *
     * @param platformModel
     * @return
     */
    private Properties loadSystemProperties(final KarafWorkingPlatformModel platformModel) {

        try {
            final Properties properties =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getParentKarafModel().getConfigurationDirectory().toFile(),
                    IKarafConstants.KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE,
                    true);

            return properties;
        } catch(final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to load configuration file: " + platformModel.getConfigurationDirectory(), e);
        }

        return new Properties();
    }
}
