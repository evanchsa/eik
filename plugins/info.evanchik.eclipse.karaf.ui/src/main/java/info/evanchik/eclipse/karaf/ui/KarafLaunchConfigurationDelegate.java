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
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchConfigurationUtils;
import info.evanchik.eclipse.karaf.ui.internal.WorkbenchServiceExtensions;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.pde.internal.launching.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.ui.launcher.EquinoxLaunchConfiguration;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafLaunchConfigurationDelegate extends EquinoxLaunchConfiguration {

    /**
     * Eclipse Equinox configuration file name
     */
    public static final String ECLIPSE_CONFIG_INI_FILE = "config.ini"; //$NON-NLS-1$

    /**
     * This property is a list of OSGi Framework Extension bundles.
     */
    public static final String OSGI_FRAMEWORK_EXTENSION = "osgi.framework.extensions"; //$NON-NLS-1$

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
     * <li>current - the classloader used to load the Equinox launcher.</li>
     * </ul>
     */
    public static final String OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY = "osgi.frameworkParentClassloader"; //$NON-NLS-1$

    /**
     * The value used to indicate that the application classloader should be
     * used as the parent for the Framework.
     */
    public static final String OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP = "app"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the install location of the platform. This setting indicates the location
     * of the basic Eclipse plug-ins and is useful if the Eclipse install is
     * disjoint. See the section on locations for more details.
     */
    public static final String OSGI_INSTALL_AREA_KEY = "osgi.install.area"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the start level value the framework will be set to at startup. The
     * default value is 6.
     */
    public static final String OSGI_START_LEVEL_KEY = "osgi.startLevel"; //$NON-NLS-1$

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     */
    public static final String OSGI_BUNDLES_KEY = "osgi.bundles"; //$NON-NLS-1$

    /**
     * The source Karaf platform model for this launch configuration
     */
    protected KarafPlatformModel karafPlatform;

    /**
     * This is the working copy of the configured Karaf platform
     */
    protected KarafWorkingPlatformModel workingKarafPlatform;

    /**
     * This is a {@link Map} of the deployed {@link Bundle}s. The key is the
     * {@code Bundle}'s location the value is its complete entry as parsed from
     * a {@code config.ini}
     */
    private final Map<String, BundleEntry> deployedBundles =
        new HashMap<String, BundleEntry>();

    /**
     * Adds the items typically found in {@code KARAF_HOME/lib} as system
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

        final List<String> arguments = new ArrayList<String>();

        for (String s : progArguments) {
            arguments.add(s);
        }

        return arguments.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * Adds the Equinox fragment bundle that implements the necessary Framework
     * Extension points (hooks) to allow Karaf to run in the workbench.
     *
     * @param configuration
     *            the launch configuration
     */
    @Override
    public String[] getVMArguments(ILaunchConfiguration configuration) throws CoreException {
        final String[] vmArguments = super.getVMArguments(configuration);

        final List<String> arguments = new ArrayList<String>();
        for (String vmArg : vmArguments) {
            arguments.add(vmArg);
        }

        arguments.add("-Deik.properties.system=" + workingKarafPlatform.getConfigurationFile("system.properties"));

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (KarafWorkbenchServiceFactory f : list) {
            arguments.addAll(f.getWorkbenchService().getVMArguments(workingKarafPlatform, configuration));
        }

        return arguments.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * This will call the proper extension points.
     */
    @Override
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor)
            throws CoreException {
        super.launch(configuration, mode, launch, monitor);

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (KarafWorkbenchServiceFactory f : list) {
            f.getWorkbenchService().launch(workingKarafPlatform, configuration, mode, launch, monitor);
        }
    }

    @Override
    protected void preLaunchCheck(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        super.preLaunchCheck(configuration, launch, monitor);

        this.karafPlatform = KarafLaunchConfigurationInitializer.findKarafPlatform(configuration, monitor);

        monitor.worked(10);

        final IPath workingArea = new Path(getConfigDir(configuration).getAbsolutePath());
        workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        monitor.worked(10);

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

        // Load config.ini
        final Properties equinoxProperties =
            KarafCorePluginUtils.loadProperties(rootDirectory.toFile(), ECLIPSE_CONFIG_INI_FILE);

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (KarafWorkbenchServiceFactory f : list) {
            final Map<String, String> m =
                f.getWorkbenchService().getAdditionalEquinoxConfiguration(workingKarafPlatform);

            equinoxProperties.putAll(m);
        }

        final String currentBundles =
            (String) equinoxProperties.get(OSGI_BUNDLES_KEY); //$NON-NLS-1$

        final String allBundles = mergeDeployedBundles(currentBundles);

        equinoxProperties.put(OSGI_BUNDLES_KEY, allBundles);

        /*
         * Set the following OSGi / Equinox properties:
         */
        final Integer defaultStartLevel =
            configuration.getAttribute(
                    IPDELauncherConstants.DEFAULT_START_LEVEL,
                    new Integer(KarafPlatformModel.KARAF_DEFAULT_BUNDLE_START_LEVEL));

        equinoxProperties.put(OSGI_START_LEVEL_KEY, defaultStartLevel.toString());

        /*
         * Set the osgi.install.area to the runtime plugins directory or the
         * directory containing Equinox?
         *
         * "/org/eclipse/osgi/3.5.0.v20090429-1630"
         */
        final IPath frameworkPath =
            new Path(karafPlatform.getState().getBundle("org.eclipse.osgi", null).getLocation()); //$NON-NLS-1$
        equinoxProperties.put(OSGI_INSTALL_AREA_KEY, frameworkPath.removeLastSegments(1).toString());

        /*
         * This is very important as it allows the boot classpath entries to
         * present their classes to the framework. Without it NoClassDefFound
         * shows up for classes like org.apache.felix.karaf.main.spi.MainService
         */
        equinoxProperties.put(OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY, OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP);

        KarafLaunchConfigurationUtils.interpolateVariables(equinoxProperties, equinoxProperties);

        LaunchConfigurationHelper.save(new File(getConfigDir(configuration), ECLIPSE_CONFIG_INI_FILE), equinoxProperties);
    }

    /**
     *
     * @param currentBundles
     * @return
     */
    private String mergeDeployedBundles(String currentBundles) throws CoreException {

        /*
         * Create a Map of all the bundles that we are going to deploy in this
         * launch of the Karaf
         */
        deployedBundles.clear();

        final List<BundleEntry> bundles = KarafCorePluginUtils.getEquinoxBundles(currentBundles);
        for(BundleEntry b : bundles) {
            deployedBundles.put(b.getBundle(), b);
        }

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (KarafWorkbenchServiceFactory f : list) {
            final List<BundleEntry> extBundles =
                f.getWorkbenchService().getAdditionalBundles(workingKarafPlatform);

            for(BundleEntry b : extBundles) {
                if(!deployedBundles.containsKey(b.getBundle())) {
                    deployedBundles.put(b.getBundle(), b);
                }
            }
        }

        return KarafCorePluginUtils.join(deployedBundles.values(), ",");
    }
}
