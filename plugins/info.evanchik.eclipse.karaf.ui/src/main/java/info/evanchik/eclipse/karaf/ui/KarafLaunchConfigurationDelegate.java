/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.ManagementSection;
import info.evanchik.eclipse.karaf.core.configuration.SystemSection;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.jmx.KarafMBeanProvider;
import info.evanchik.eclipse.karaf.core.jmx.MBeanProvider;
import info.evanchik.eclipse.karaf.core.jmx.MBeanServerConnectionDescriptor;
import info.evanchik.eclipse.karaf.core.jmx.MBeanServerConnectionJob;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.jmx.KarafJMXConnectorActivator;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchConfigurationUtils;
import info.evanchik.eclipse.karaf.ui.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.ui.provider.internal.KarafRuntimeDataProvider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.pde.internal.ui.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.internal.ui.launcher.LauncherUtils;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafLaunchConfigurationDelegate extends EquinoxLaunchConfiguration {

    /**
     * Eclipse Equinox configuration file name
     */
    public static String ECLIPSE_CONFIG_INI_FILE = "config.ini"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * The classloader type to use as the parent classloader for the the
     * Framework. The valid types are the following:<br>
     * <br>
     * <ul>
     * <li>app - the application classloader.</li>
     * <li>boot - the boot classloader.</li>
     * <li>ext - the extension classloader.</li>
     * <li>current - the classloader used to load the equinox launcher.</li>
     * </ul>
     */
    public static String OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY = "osgi.frameworkParentClassloader"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the install location of the platform. This setting indicates the location
     * of the basic Eclipse plug-ins and is useful if the Eclipse install is
     * disjoint. See the section on locations for more details.
     */
    public static String OSGI_INSTALL_AREA_KEY = "osgi.install.area"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the start level value the framework will be set to at startup. The
     * default value is 6.
     */
    public static String OSGI_START_LEVEL_KEY = "osgi.startLevel"; //$NON-NLS-1$

    /**
     * The source Karaf platform model for this launch configuration
     */
    protected KarafPlatformModel karafPlatform;

    /**
     * This is the working copy of the configured Karaf platform
     */
    protected KarafPlatformModel workingKarafPlatform;

    /**
     * The JMX port setup by this launch configuration
     */
    private int jmxPort;

    private KarafMBeanProvider mbeanProvider;

    private MBeanServerConnectionJob mbeanConnectionJob;

    private RuntimeDataProvider runtimeDataProvider;

    private ServiceRegistration karafRuntimeDataProviderServiceRegistrtion;

    /**
     * Adds the items typically found in {@code KARAF_HOME/lib} as boot
     * classpath entries.<br>
     * <br>
     * <b>This requires that
     * {@link KarafLaunchConfigurationDelegate#OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY}
     * be set to "{@code app}"</b>
     *
     * @param configuration
     *            the launch configuration
     */
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
        final String[] mainClasspath = super.getClasspath(configuration);

        final List<String> classpath = karafPlatform.getBootClasspath();

        for (String s : mainClasspath) {
            classpath.add(s);
        }

        return classpath.toArray(new String[0]);
    }

    @Override
    public String[] getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
        final String[] progArguments = super.getProgramArguments(configuration);

        buildEquinoxConfiguration(configuration);

        return progArguments;
    }

    /**
     * Adds the Equinox fragment bundle that implements the necessary Framework
     * Extension points (hooks) to allow Karaf to run in the workbench.
     *
     * @param configuration
     *            the launch configuration
     */
    @Override
    public String[] getVMArguments(ILaunchConfiguration configuration) throws CoreException {
        final String FRAMEWORK_EXTENSION_PREFIX = "-Dosgi.framework.extensions=";

        final String[] vmArguments = super.getVMArguments(configuration);

        final List<String> arguments = new ArrayList<String>();

        String frameworkExtension = null;

        // Process all arguments, preserving all but the framework extension arg
        // which is augmented to include the proper hook bundle
        for (String vmArg : vmArguments) {
            if (vmArg.startsWith(FRAMEWORK_EXTENSION_PREFIX) && !vmArg.contains(KarafLaunchConfigurationInitializer.KARAF_HOOK_PLUGIN_ID)) {

                frameworkExtension = vmArg.concat("," + KarafLaunchConfigurationInitializer.KARAF_HOOK_PLUGIN_ID);
            } else {
                arguments.add(vmArg);
            }
        }

        // Did not find the framework extension property
        if (frameworkExtension == null) {
            arguments.add(FRAMEWORK_EXTENSION_PREFIX + KarafLaunchConfigurationInitializer.KARAF_HOOK_PLUGIN_ID);
        }

        addBootstrapSystemProperties(configuration, arguments);

        /*
         * Ensure that the RMI registry port for the JMX connector is unique
         */

        final int jmxRegistryPort = SocketUtil.findFreePort();

        if (jmxRegistryPort == -1) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX RMI Registry"));
        }

        final ManagementSection managementSection = (ManagementSection) Platform.getAdapterManager().getAdapter(workingKarafPlatform,
                ManagementSection.class);

        managementSection.load();

        managementSection.setPort(jmxRegistryPort);

        managementSection.save();

        /*
         * Ensure the Remote JMX connector port is unique
         */

        jmxPort = SocketUtil.findFreePort();

        if (jmxPort == -1) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                    "Could not find suitable TCP/IP port for JMX connection"));
        }

        arguments.add("-Dcom.sun.management.jmxremote.authenticate=false");
        arguments.add("-Dcom.sun.management.jmxremote.port=" + new Integer(jmxPort).toString());
        arguments.add("-Dcom.sun.management.jmxremote.ssl=false");

        return arguments.toArray(new String[0]);
    }

    /**
     * Overridden to provide the necessary hooks to register the JMX connection
     */
    @Override
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor)
            throws CoreException {
        super.launch(configuration, mode, launch, monitor);

        try {
            startMBeanConnectionJob(configuration);
        } catch (MalformedURLException e) {
            KarafUIPluginActivator.getLogger().error("Unable to connect to JMX endpoint on Karaf instance", e);

            // Terminate? launch.terminate()
        }

        registerDebugEventListener(launch);

        registerLaunchListener(launch);
    }

    /**
     * Returns the value of the System property {@code karaf.base}, preferring
     * the user specified value in the {@link ILaunchConfiguration}.
     *
     * @param configuration
     *            the launch configuration data
     * @return the fully qualified path to {@code karaf.base}
     */
    protected String getKarafBase(ILaunchConfiguration configuration) {
        return new Path(getConfigDir(configuration).getPath()).toString();
    }

    /**
     * Returns the value of the System property {@code karaf.home}, preferring
     * the user specified value in the {@link ILaunchConfiguration}.
     *
     * @param configuration
     *            the launch configuration data
     * @return the fully qualified path to {@code karaf.home}
     */
    protected String getKarafHome(ILaunchConfiguration configuration) {
        return new Path(getConfigDir(configuration).getPath()).toString();
    }

    /**
     * Loads the Karaf platform.
     *
     * @see KarafLaunchConfigurationInitializer#findKarafPlatform(ILaunchConfiguration,
     *      IProgressMonitor)
     *
     * @param configuration
     * @param launch
     * @param monitor
     * @throws CoreException
     */
    protected void loadKarafPlatform(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        this.karafPlatform = KarafLaunchConfigurationInitializer.findKarafPlatform(configuration, monitor);

        monitor.worked(10);
    }

    @Override
    protected void preLaunchCheck(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        super.preLaunchCheck(configuration, launch, monitor);

        loadKarafPlatform(configuration, launch, monitor);

        KarafLaunchConfigurationInitializer.synchronizeHooksWithPlatform(this.karafPlatform);

        monitor.worked(10);

        final IPath workingArea = new Path(getConfigDir(configuration).getAbsolutePath());
        workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        monitor.worked(10);

        addConfiguredSystemProperties(configuration);

        monitor.worked(10);
    }

    /**
     * Adds all necessary System properties to boot the Karaf platform
     *
     * @param configuration
     *            the launch configuration
     * @param arguments
     *            the current {@link List} of arguments
     * @throws CoreException
     *             thrown if there is a problem adding to the arguments list
     */
    private void addBootstrapSystemProperties(ILaunchConfiguration configuration, List<String> arguments) throws CoreException {
        arguments.add("-D" + KarafPlatformModel.KARAF_BASE_PROP + "=" + getKarafBase(configuration));
        arguments.add("-D" + KarafPlatformModel.KARAF_HOME_PROP + "=" + getKarafHome(configuration));
    }

    /**
     * Computes additional system properties for the Karaf environment based on
     * current launcher configuration elements.
     *
     * @param configuration
     *            the launch configuration data
     */
    private void addConfiguredSystemProperties(ILaunchConfiguration configuration) throws CoreException {

        final String karafBase = getKarafBase(configuration);
        final String karafHome = getKarafHome(configuration);

        final Boolean startLocalConsole = configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE,
                true);
        final Boolean startRemoteConsole = configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                false);

        final File javaLoggingFile = new File(karafBase, "java.util.logging.properties"); //$NON-NLS-1$

        final SystemSection systemSection = (SystemSection) Platform.getAdapterManager().getAdapter(workingKarafPlatform,
                SystemSection.class);

        systemSection.load();

        systemSection.setProperty(KarafPlatformModel.KARAF_BASE_PROP, karafBase);
        systemSection.setProperty(KarafPlatformModel.KARAF_HOME_PROP, karafHome);
        systemSection.setProperty("java.util.logging.config.file", javaLoggingFile.getAbsolutePath()); //$NON-NLS-1$
        systemSection.setProperty("karaf.startLocalConsole", startLocalConsole.toString()); //$NON-NLS-1$
        systemSection.setProperty("karaf.startRemoteShell", startRemoteConsole.toString()); //$NON-NLS-1$

        systemSection.save();
    }

    /**
     * Adds the {@link KarafJMXConnectorActivator#PLUGIN_ID} plugin to the
     * runtime
     *
     * @param equinoxProperties
     *            the {@link Properties} as loaded from the config.ini
     */
    private void addJMXConnectorServiceBundle(final Properties equinoxProperties) throws CoreException {

        final Bundle jmxConnectorService = Platform.getBundle(KarafJMXConnectorActivator.PLUGIN_ID);
        if (jmxConnectorService == null) {
            throw new CoreException(LauncherUtils.createErrorStatus("Unable to locate Apache Felix Karaf JMX Connector Service bundle: "
                    + KarafJMXConnectorActivator.PLUGIN_ID));
        }

        final String bundleLocation = jmxConnectorService.getLocation();
        String osgiBundles = (String) equinoxProperties.get("osgi.bundles"); //$NON-NLS-1$

        final int locationIndex = osgiBundles.indexOf(bundleLocation);
        if (locationIndex == -1) {
            final BundleEntry entry = new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            osgiBundles = osgiBundles.concat("," + entry.toString()); //$NON-NLS-1$
            equinoxProperties.put("osgi.bundles", osgiBundles); //$NON-NLS-1$
        }
    }

    /**
     * Construct a config.ini from the Karaf configuration of this launcher.
     *
     * @param configuration
     *            the launch configuration
     * @throws CoreException
     *             if there is a problem reading or writing the configuration
     *             file
     */
    private void buildEquinoxConfiguration(ILaunchConfiguration configuration) throws CoreException {

        final IPath rootDirectory = workingKarafPlatform.getRootDirectory();
        final IPath configDirectory = workingKarafPlatform.getConfigurationDirectory();

        // Load config.ini and add the KarafStarterService bundle
        final Properties equinoxProperties = KarafCorePluginUtils.loadProperties(rootDirectory.toFile(), ECLIPSE_CONFIG_INI_FILE);
        final Properties currentConfig = KarafCorePluginUtils.loadProperties(configDirectory.toFile(),
                KarafPlatformModel.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE);

        /*
         * Populate the config.ini wtih all of the typical Karaf properties that
         * are not found in the System properties
         */
        equinoxProperties.putAll(currentConfig);

        addJMXConnectorServiceBundle(equinoxProperties);

        /*
         * Copy these properties so that they can be used to interpolate the
         * values found in Karaf's config.properties
         */
        final SystemSection systemSection = (SystemSection) Platform.getAdapterManager().getAdapter(workingKarafPlatform,
                SystemSection.class);

        systemSection.load();

        equinoxProperties.put(KarafPlatformModel.KARAF_BASE_PROP, systemSection.getProperty(KarafPlatformModel.KARAF_BASE_PROP));
        equinoxProperties.put(KarafPlatformModel.KARAF_HOME_PROP, systemSection.getProperty(KarafPlatformModel.KARAF_HOME_PROP));

        /*
         * Adds the $TARGET_HOME/runtimes/karaf/plugins directory to the default
         * bundle.locations search space
         */
        equinoxProperties.put(KarafPlatformModel.KARAF_BUNDLE_LOCATIONS_PROP, karafPlatform.getPluginRootDirectory().toOSString());

        /*
         * Set the following OSGi / Equinox properties:
         */
        final Integer defaultStartLevel = configuration.getAttribute(IPDELauncherConstants.DEFAULT_START_LEVEL, new Integer(
                KarafPlatformModel.KARAF_DEFAULT_BUNDLE_START_LEVEL));
        equinoxProperties.put(OSGI_START_LEVEL_KEY, defaultStartLevel.toString()); //$NON-NLS-1$

        /*
         * Set the osgi.install.area to the runtime plugins directory or the
         * directory containing Equinox?
         *
         * "/org/eclipse/osgi/3.5.0.v20090429-1630"
         */
        final IPath frameworkPath = new Path(karafPlatform.getState().getBundle("org.eclipse.osgi", null).getLocation());
        equinoxProperties.put(OSGI_INSTALL_AREA_KEY, frameworkPath.removeLastSegments(1).toString()); //$NON-NLS-1$

        /*
         * This is very important as it allows the boot classpath entries to
         * present their classes to the framework. Without it NoClassDefFound
         * shows up for classes like org.apache.felix.karaf.main.spi.MainService
         */
        equinoxProperties.put(OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY, "app"); //$NON-NLS-1$

        KarafLaunchConfigurationUtils.interpolateVariables(equinoxProperties, equinoxProperties);

        LaunchConfigurationHelper.save(new File(getConfigDir(configuration), ECLIPSE_CONFIG_INI_FILE), equinoxProperties);
    }

    /**
     * Registers an event listener on the debug session that responds to
     * {@link DebugEvent.TERMINATE} events. This will stop the MBBean connection
     * job, the {@link RuntimeDataProvider} and the {@link MBeanProvider}
     *
     * @param launch
     *            the launch process
     */
    private void registerDebugEventListener(final ILaunch launch) {
        final IProcess process = launch.getProcesses()[0];

        final IDebugEventSetListener processListener = new IDebugEventSetListener() {
            public void handleDebugEvents(final DebugEvent[] events) {
                if (events == null) {
                    return;
                }

                final int size = events.length;
                for (int i = 0; i < size; i++) {
                    if (process != null && process.equals(events[i].getSource()) && events[i].getKind() == DebugEvent.TERMINATE) {
                        if (mbeanConnectionJob != null) {
                            mbeanConnectionJob.cancel();
                        }

                        if (runtimeDataProvider != null) {
                            runtimeDataProvider.stop();
                        }

                        if (mbeanProvider != null) {
                            mbeanProvider.close();
                        }
                    }
                }

            }
        };

        DebugPlugin.getDefault().addDebugEventListener(processListener);
    }

    /**
     * Registers services against the running Karaf instance so that the Eclipse
     * workbench can control and retrieve information from the process.
     */
    private void registerKarafWorkbenchServices() {
        final BundleContext bundleContext = KarafUIPluginActivator.getDefault().getBundle().getBundleContext();

        final Dictionary<String, Object> dictionary = new Hashtable<String, Object>();

        karafRuntimeDataProviderServiceRegistrtion = bundleContext.registerService(RuntimeDataProvider.class.getName(),
                runtimeDataProvider, dictionary);
    }

    /**
     * Registers a listener for launch configuration management. When the launch
     * configuration is removed from the debug view this will cleanup the
     * services registered during this debug session.
     *
     * @param launch
     *            the launch process
     */
    private void registerLaunchListener(final ILaunch launch) {
        final ILaunchListener launchListener = new ILaunchListener() {

            public void launchAdded(ILaunch l) {
            }

            public void launchChanged(ILaunch l) {
            }

            public void launchRemoved(ILaunch l) {
                if (karafRuntimeDataProviderServiceRegistrtion != null & l.equals(launch)) {
                    karafRuntimeDataProviderServiceRegistrtion.unregister();
                    karafRuntimeDataProviderServiceRegistrtion = null;
                }
            }

        };

        DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
    }

    /**
     * Starts a background system job that will connect to the Karaf MBeanServer
     *
     * @param configuration
     *            the launch configuration
     * @throws MalformedURLException
     *             if the MBeanServer URL is invalid
     * @throws CoreException
     *             if there is a general problem scheduling the {@link Job}
     */
    private void startMBeanConnectionJob(final ILaunchConfiguration configuration) throws MalformedURLException, CoreException {
        final MBeanServerConnectionDescriptor descriptor = MBeanServerConnectionDescriptor.createStandardDescriptor(
                configuration.getName(), "localhost", jmxPort, null, null); // $NON-NLS-1$

        final String memento = configuration.getMemento();
        mbeanConnectionJob = new MBeanServerConnectionJob(configuration.getName(), descriptor);

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

                try {
                    mbeanProvider = new KarafMBeanProvider(mbeanConnectionJob.getJmxClient());
                    mbeanProvider.open(memento);
                } catch (IOException e) {
                    KarafUIPluginActivator.getLogger().error("Unable to create MBeanProvider from JMXConnector", e);
                    return;
                }

                runtimeDataProvider = new KarafRuntimeDataProvider(configuration.getName(), mbeanProvider);
                runtimeDataProvider.start();

                registerKarafWorkbenchServices();
            }

        };

        mbeanConnectionJob.addJobChangeListener(listener);
        mbeanConnectionJob.schedule(MBeanServerConnectionJob.DEFAULT_INITIAL_SCHEDULE_DELAY);
    }

}
