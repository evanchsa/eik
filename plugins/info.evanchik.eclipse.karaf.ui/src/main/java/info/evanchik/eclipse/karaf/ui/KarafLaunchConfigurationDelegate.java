/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 *  Timo Naroska - proper console support for Windows
 */
package info.evanchik.eclipse.karaf.ui;

import info.evanchik.eclipse.karaf.core.IKarafConstants;
import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.SystemBundleNames;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchConfigurationUtils;
import info.evanchik.eclipse.karaf.ui.internal.WorkbenchServiceExtensions;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall3;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.launching.EquinoxLaunchConfiguration;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafLaunchConfigurationDelegate extends EquinoxLaunchConfiguration {

    /**
     * Java Specification Version system property
     */
    public static final String JAVA_SPECIFICATION_VERSION = "java.specification.version"; //$NON-NLS-1$

    /**
     * Eclipse Equinox configuration file name
     */
    public static final String ECLIPSE_CONFIG_INI_FILE = "config.ini"; //$NON-NLS-1$

    /**
     * The classloader type to use as the parent {@link ClassLoader} of the
     * context classloader used by the Framework. The valid types are the
     * following:<br>
     * <br>
     * <ul>
     * <li>app - the application classloader.</li>
     * <li>boot - the boot classloader.</li>
     * <li>ext - the extension classloader.</li>
     * <li>fwk - the framework classloader.</li>
     * <li>ccl - the original context classloader that was set when the
     * framework launched (default value).</li>
     * </ul>
     */
    public static final String OSGI_CONTEXT_CLASSLOADER_PARENT_KEY = "osgi.contextClassLoaderParent"; //$NON-NLS-1$

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
     * The classloader type to use as the parent {@link ClassLoader} for all
     * bundles installed in the Framework. The valid types are the following:<br>
     * <br>
     * <ul>
     * <li>app - the application classloader.</li>
     * <li>boot - the boot classloader.</li>
     * <li>ext - the extension classloader.</li>
     * <li>fwk - the framework classloader.</li>
     * </ul>
     */
    public static final String OSGI_PARENT_CLASSLOADER_KEY = "osgi.parentClassloader"; //$NON-NLS-1$

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
	 * Saves a properties file
	 *
	 * @param file
	 * @param properties
	 */
    public static void save(final File file, final Properties properties) {
        try {
            final FileOutputStream stream = new FileOutputStream(file);
            properties.store(stream, "Configuration File"); //$NON-NLS-1$
            stream.flush();
            stream.close();
        } catch (final IOException e) {
            //PDECore.logException(e);
        }
    }
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
    public String[] getClasspath(final ILaunchConfiguration configuration) throws CoreException {
        final String[] mainClasspath = super.getClasspath(configuration);

        final List<String> classpath = karafPlatform.getBootClasspath();

        for (final String s : mainClasspath) {
            classpath.add(s);
        }

        return classpath.toArray(new String[0]);
    }

    @Override
    public String[] getProgramArguments(final ILaunchConfiguration configuration) throws CoreException {
        final String[] progArguments = super.getProgramArguments(configuration);

        buildEquinoxConfiguration(configuration);

        final List<String> arguments = new ArrayList<String>();

        for (final String s : progArguments) {
            arguments.add(s);
        }

        return arguments.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     * <p>
     *
     * @param configuration
     *            the launch configuration
     */
    @Override
    public String[] getVMArguments(final ILaunchConfiguration configuration) throws CoreException {
        final String[] vmArguments = super.getVMArguments(configuration);

        final List<String> arguments = new ArrayList<String>();
        for (final String vmArg : vmArguments) {
            arguments.add(vmArg);
        }

        addJavaExtensionsArguments(configuration, arguments);

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "eik.properties.system", //$NON-NLS-1$
                        workingKarafPlatform.getConfigurationFile("system.properties").toString())); //$NON-NLS-1$

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (final KarafWorkbenchServiceFactory f : list) {
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

        for (final KarafWorkbenchServiceFactory f : list) {
            f.getWorkbenchService().launch(workingKarafPlatform, configuration, mode, launch, monitor);
        }
    }

    @Override
    protected void preLaunchCheck(final ILaunchConfiguration configuration, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
        super.preLaunchCheck(configuration, launch, monitor);

        this.karafPlatform = KarafLaunchConfigurationInitializer.findKarafPlatform(configuration, monitor);

        monitor.worked(10);

        final IPath workingArea = new Path(getConfigDir(configuration).getAbsolutePath());
        workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        monitor.worked(10);
    }

    /**
     * Adds the {@code java.endorsed.dirs} and {@code java.ext.dirs} VM
     * arguments for the Karaf platform
     *
     * @param configuration
     *            the launch configuration
     * @param arguments
     *            the current list of arguments
     * @throws CoreException
     *             thrown if the {@link VMInstall} cannot be computed
     */
    private void addJavaExtensionsArguments(
            final ILaunchConfiguration configuration,
            final List<String> arguments) throws CoreException {

        final List<String> endorsedDirs = new ArrayList<String>();
        endorsedDirs.add(workingKarafPlatform.getParentKarafModel().getRootDirectory().append("lib/endorsed").toString()); //$NON-NLS-1$

        final List<String> extDirs = new ArrayList<String>();
        extDirs.add(workingKarafPlatform.getParentKarafModel().getRootDirectory().append("lib/ext").toString()); //$NON-NLS-1$

        final IVMInstall vmInstall = JavaRuntime.computeVMInstall(configuration);
        final File vmRootDirectory = vmInstall.getInstallLocation();
        if (vmRootDirectory != null) {
            endorsedDirs.add(new File(vmRootDirectory, "jre/lib/endorsed").getAbsolutePath()); //$NON-NLS-1$
            endorsedDirs.add(new File(vmRootDirectory, "lib/endorsed").getAbsolutePath()); //$NON-NLS-1$

            extDirs.add(new File(vmRootDirectory, "jre/lib/ext").getAbsolutePath()); //$NON-NLS-1$
            extDirs.add(new File(vmRootDirectory, "lib/ext").getAbsolutePath()); //$NON-NLS-1$
        }

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "java.endorsed.dirs", //$NON-NLS-1$
                        KarafCorePluginUtils.join(endorsedDirs, ":"))); //$NON-NLS-1$

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "java.ext.dirs", //$NON-NLS-1$
                        KarafCorePluginUtils.join(extDirs, ":"))); //$NON-NLS-1$
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
    private void buildEquinoxConfiguration(final ILaunchConfiguration configuration) throws CoreException {

        final IPath rootDirectory = workingKarafPlatform.getRootDirectory();

        // Load config.ini
        final Properties equinoxProperties =
            KarafCorePluginUtils.loadProperties(rootDirectory.toFile(), ECLIPSE_CONFIG_INI_FILE);

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (final KarafWorkbenchServiceFactory f : list) {
            final Map<String, String> m =
                f.getWorkbenchService().getAdditionalEquinoxConfiguration(workingKarafPlatform);

            equinoxProperties.putAll(m);
        }

        final String currentBundles =
            (String) equinoxProperties.get(OSGI_BUNDLES_KEY);

        final String allBundles = mergeDeployedBundles(currentBundles);

        equinoxProperties.put(OSGI_BUNDLES_KEY, allBundles);

        /*
         * Set the following OSGi / Equinox properties:
         */
        if (!equinoxProperties.containsKey(OSGI_START_LEVEL_KEY)) {
	        final Integer defaultStartLevel =
	            configuration.getAttribute(
	                    IPDELauncherConstants.DEFAULT_START_LEVEL,
	                    new Integer(IKarafConstants.KARAF_DEFAULT_BUNDLE_START_LEVEL));

	        equinoxProperties.put(OSGI_START_LEVEL_KEY, defaultStartLevel.toString());
        }

        /*
         * Set the osgi.install.area to the runtime plugins directory or the
         * directory containing Equinox?
         *
         * "/org/eclipse/osgi/3.5.0.v20090429-1630"
         */
        final IPath frameworkPath =
            new Path(karafPlatform.getState().getBundle(SystemBundleNames.EQUINOX.toString(), null).getLocation());
        equinoxProperties.put(OSGI_INSTALL_AREA_KEY, frameworkPath.removeLastSegments(1).toString());

        final String javaSpecificationVersion = getJavaRuntimeSpecificationVersion(configuration);
        equinoxProperties.put(JAVA_SPECIFICATION_VERSION, javaSpecificationVersion);

        /*
         * This is very important as it allows the boot classpath entries to
         * present their classes to the framework. Without it NoClassDefFound
         * shows up for classes like org.apache.karaf.jaas.boot.ProxyLoginModule
         */
        equinoxProperties.put(OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY, OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP);
        equinoxProperties.put(OSGI_CONTEXT_CLASSLOADER_PARENT_KEY, OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP);
        equinoxProperties.put(OSGI_PARENT_CLASSLOADER_KEY, OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP);

        KarafLaunchConfigurationUtils.interpolateVariables(equinoxProperties, equinoxProperties);

        save(new File(getConfigDir(configuration), ECLIPSE_CONFIG_INI_FILE), equinoxProperties);
    }

    /**
     * Determines the Java Specification Version of the Java Virtual Machine
     * that will execute Karaf. If the {@link VMInstall} does not support
     * evaluating system properties then the workbench's specification version
     * is used.
     *
     * @param configuration
     *            the launch configuration
     * @return the Java Specification Version (e.g "1.6" or "1.5")
     * @throws CoreException
     *             if there is a problem determining the Java Specification
     *             Version of the {@code VMInstall}
     */
    private String getJavaRuntimeSpecificationVersion(final ILaunchConfiguration configuration) throws CoreException {
        final IVMInstall vmInstall = JavaRuntime.computeVMInstall(configuration);
        if (!(vmInstall instanceof IVMInstall3)) {
            return System.getProperty(JAVA_SPECIFICATION_VERSION);
        }

        final IVMInstall3 vmInstall3 = (IVMInstall3) vmInstall;

        @SuppressWarnings("unchecked")
        final Map<String, String> properties =
            vmInstall3.evaluateSystemProperties(new String[] { JAVA_SPECIFICATION_VERSION }, new NullProgressMonitor());

        return properties.get(JAVA_SPECIFICATION_VERSION);
    }

    /**
     *
     * @param currentBundles
     * @return
     */
    private String mergeDeployedBundles(final String currentBundles) throws CoreException {

        /*
         * Create a Map of all the bundles that we are going to deploy in this
         * launch of the Karaf
         */
        deployedBundles.clear();

        final List<BundleEntry> bundles = KarafCorePluginUtils.getEquinoxBundles(currentBundles);
        for(final BundleEntry b : bundles) {
            deployedBundles.put(b.getBundle(), b);
        }

        final List<KarafWorkbenchServiceFactory> list =
            WorkbenchServiceExtensions.getLaunchCustomizerFactories();

        for (final KarafWorkbenchServiceFactory f : list) {
            final List<BundleEntry> extBundles =
                f.getWorkbenchService().getAdditionalBundles(workingKarafPlatform);

            for(final BundleEntry b : extBundles) {
                if(!deployedBundles.containsKey(b.getBundle())) {
                    deployedBundles.put(b.getBundle(), b);
                }
            }
        }

        return KarafCorePluginUtils.join(deployedBundles.values(), ",");  //$NON-NLS-1$
    }
}
