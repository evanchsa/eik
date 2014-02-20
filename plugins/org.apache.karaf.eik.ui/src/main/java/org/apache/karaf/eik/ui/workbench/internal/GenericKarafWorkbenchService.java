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
package org.apache.karaf.eik.ui.workbench.internal;

import org.apache.karaf.eik.core.IKarafConstants;
import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.configuration.ManagementSection;
import org.apache.karaf.eik.core.equinox.BundleEntry;
import org.apache.karaf.eik.core.model.GenericKarafPlatformModel;
import org.apache.karaf.eik.core.shell.KarafSshConnectionUrl;
import org.apache.karaf.eik.core.shell.KarafSshShellConnection;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafLaunchConfigurationConstants;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.console.KarafRemoteConsole;
import org.apache.karaf.eik.ui.workbench.KarafWorkbenchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

public class GenericKarafWorkbenchService implements KarafWorkbenchService {

    private final class KarafConsoleLaunchListener implements ILaunchListener {

        /**
         * Returns the console for the given {@link IProcess}, or {@code null}
         * if none.
         *
         * @param process the {@code IProcess} whose console is to be found
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
         * @param process the {@code IProcess} whose document is to be retrieved
         * @return the {@code IDocument} for the specified {@code IProcess} or
         * {@code null} if one could not be found
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

                final KarafPlatformModel karafPlatform =
                        (KarafPlatformModel) launch.getLaunchConfiguration().getAdapter(KarafPlatformModel.class);

                final KarafSshConnectionUrl sshConnectionUrl =
                        (KarafSshConnectionUrl) karafPlatform.getAdapter(KarafSshConnectionUrl.class);

                final KarafSshShellConnection.Credentials credentials;

                try {
                    final String username =
                            launch.getLaunchConfiguration().getAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_USERNAME, "karaf");
                    final String password =
                            launch.getLaunchConfiguration().getAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_PASSWORD, "karaf");

                    credentials = new KarafSshShellConnection.Credentials(username, password);
                } catch (final CoreException e) {
                    throw new AssertionError(e);
                }

                final KarafRemoteConsole remoteConsole = new KarafRemoteConsole(
                        process,
                        sshConnectionUrl,
                        credentials,
                        new ConsoleColorProvider(),
                        launch.getLaunchConfiguration().getName(),
                        encoding);

                remoteConsole.setAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS, process);

                ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{remoteConsole});
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
                    manager.removeConsoles(new IConsole[]{console});
                }
            }
        }

        /**
         * Determines whether or not the {@link ILaunch} is from an EIK launch
         *
         * @param launch the {@code ILaunch} to evaluate
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
                "org.apache.karaf.eik.app",
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

        for (final String b : bundles) {

            // If the bundle is already present in the platform, don't add it
            if (platformModel.getState().getBundle(b, null) != null) {
                continue;
            }

            final String bundleLocation =
                    KarafCorePluginUtils.getBundleLocation(b);

            if (bundleLocation != null) {
                final BundleEntry entry =
                        new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

                bundleEntries.add(entry);
            }
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
                            platformModel.getParentKarafModel().getConfigurationDirectory().toFile(),
                            IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE,
                            true);

            final Properties systemProperties = createLaunchSystemProperties(platformModel, configuration);
            currentConfig.putAll(systemProperties);

            PropertyUtils.interpolateVariables(currentConfig, currentConfig);

            for (final Map.Entry<Object, Object> e : currentConfig.entrySet()) {
                equinoxProperties.put((String) e.getKey(), (String) e.getValue());
            }
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to load configuration file: " + platformModel.getParentKarafModel().getConfigurationDirectory(), e);
        }

        addEclipseObrFile(platformModel, equinoxProperties);

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
            throws CoreException {
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
            vmArgs.append(" -Declipse.application=org.apache.karaf.eik.app.KarafMain"); //$NON-NLS-1$
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
            final IProgressMonitor monitor) throws CoreException {
        if (!platformModel.getParentKarafModel().getClass().equals(GenericKarafPlatformModel.class)) {
            return;
        }

        configureKarafFeatures(platformModel, configuration);
        configureJMXConnector(platformModel);
    }

    private void addEclipseObrFile(
            final KarafWorkingPlatformModel platformModel,
            final Map<String, String> equinoxProperties) {
        final IKarafProject karafProject = (IKarafProject) platformModel.getAdapter(IKarafProject.class);
        // TODO: This should be factored out somehow
        final IFile file = karafProject.getFile("platform/eclipse.obr.xml");
        final IPath path = file.getRawLocation();

        final String obr = equinoxProperties.get(IKarafConstants.KARAF_OBR_REPOSITORY_PROP);
        if (obr.indexOf("eclipse.obr.xml") == -1) {
            final String obrUrls;
            if (obr.trim().length() > 1) {
                obrUrls = obr + "," + "file://" + path.toFile().getAbsolutePath();
            } else {
                obrUrls = "file://" + path.toFile().getAbsolutePath();
            }
            equinoxProperties.put(
                    IKarafConstants.KARAF_OBR_REPOSITORY_PROP,
                    obrUrls);
        }
    }

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

        // Add ref to karaf.etc for karaf 3.0.0
        systemProperties.put(
                IKarafConstants.KARAF_ETC_PROP,
                platformModel.getParentKarafModel().getRootDirectory().append("etc").toOSString());

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

    private Properties loadSystemProperties(final KarafWorkingPlatformModel platformModel) {

        try {
            final Properties properties =
                    KarafCorePluginUtils.loadProperties(
                            platformModel.getParentKarafModel().getConfigurationDirectory().toFile(),
                            IKarafConstants.KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE,
                            true);

            return properties;
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to load configuration file: " + platformModel.getConfigurationDirectory(), e);
        }

        return new Properties();
    }

}
