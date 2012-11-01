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
package org.apache.karaf.eik.ui;

import org.apache.karaf.eik.core.IKarafConstants;
import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import org.apache.karaf.eik.core.SystemBundleNames;
import org.apache.karaf.eik.core.equinox.BundleEntry;
import org.apache.karaf.eik.core.model.WorkingKarafPlatformModel;
import org.apache.karaf.eik.ui.internal.WorkbenchServiceExtensions;
import org.apache.karaf.eik.ui.workbench.KarafWorkbenchServiceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
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

public class KarafLaunchConfigurationDelegate extends EquinoxLaunchConfiguration {

    /**
     * Eclipse Equinox configuration file name
     */
    public static final String ECLIPSE_CONFIG_INI_FILE = "config.ini";

    /**
     * Java Specification Version system property
     */
    public static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     */
    public static final String OSGI_BUNDLES_KEY = "osgi.bundles";

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
    public static final String OSGI_CONTEXT_CLASSLOADER_PARENT_KEY = "osgi.contextClassLoaderParent";

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     */
    public static final String OSGI_EXTRA_SYSTEM_PACKAGES_KEY = "org.osgi.framework.system.packages.extra";

    /**
     * This property is a list of OSGi Framework Extension bundles.
     */
    public static final String OSGI_FRAMEWORK_EXTENSION = "osgi.framework.extensions";

    /**
     * The value used to indicate that the application classloader should be
     * used as the parent for the Framework.
     */
    public static final String OSGI_FRAMEWORK_PARENT_CLASSLOADER_APP = "app";

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
    public static final String OSGI_FRAMEWORK_PARENT_CLASSLOADER_KEY = "osgi.frameworkParentClassloader";

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the install location of the platform. This setting indicates the location
     * of the basic Eclipse plug-ins and is useful if the Eclipse install is
     * disjoint. See the section on locations for more details.
     */
    public static final String OSGI_INSTALL_AREA_KEY = "osgi.install.area";

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
    public static final String OSGI_PARENT_CLASSLOADER_KEY = "osgi.parentClassloader";

    /**
     * From the Equinox runtime documentation:<br>
     * <br>
     * the start level value the framework will be set to at startup. The
     * default value is 6.
     */
    public static final String OSGI_START_LEVEL_KEY = "osgi.startLevel";

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
    public String[] getClasspath(final ILaunchConfiguration configuration) throws CoreException {
        final String[] mainClasspath = super.getClasspath(configuration);

        final List<String> classpath = new ArrayList<String>(Arrays.asList(mainClasspath));
        final List<String> karafModelClasspath = fixKarafJarClasspathEntry(classpath);
        classpath.addAll(karafModelClasspath);

        augmentClasspathWithEquinox(classpath);

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
     * This will call the proper EIK extension points.
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

        this.karafPlatform = KarafPlatformModelRegistry.findActivePlatformModel();

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
                        KarafCorePluginUtils.join(endorsedDirs, File.pathSeparator)));

        arguments.add(
                KarafCorePluginUtils.constructSystemProperty(
                        "java.ext.dirs", //$NON-NLS-1$
                        KarafCorePluginUtils.join(extDirs, File.pathSeparator)));
    }

    /**
     * Augment the boot classpath with the Equinox JAR that is running Karaf.
     * <p>
     * There is normally an OSGi Framework JAR as a boot classpath item when
     * Karaf launches. This preserves that behavior.
     *
     * @param classpath
     *            the boot classpath
     */
    private void augmentClasspathWithEquinox(final List<String> classpath) {
        final IPath frameworkPath =
            new Path(karafPlatform.getState().getBundle(SystemBundleNames.EQUINOX.toString(), null).getLocation());

        classpath.add(frameworkPath.toFile().getAbsolutePath());
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
                f.getWorkbenchService().getAdditionalEquinoxConfiguration(workingKarafPlatform, configuration);

            equinoxProperties.putAll(m);
        }

        final String currentBundles =
            (String) equinoxProperties.get(OSGI_BUNDLES_KEY);

        final String allBundles = mergeDeployedBundles(currentBundles, configuration);

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

        /*
         * Eclipse 3.7 (Indigo) has a bug that does not ignore the empty
         * org.osgi.framework.system.packages.extra property
         */
        final String extraSystemPackages = (String) equinoxProperties.get(OSGI_EXTRA_SYSTEM_PACKAGES_KEY);
        if (extraSystemPackages != null && extraSystemPackages.trim().isEmpty()) {
            equinoxProperties.remove(OSGI_EXTRA_SYSTEM_PACKAGES_KEY);
        }

        PropertyUtils.interpolateVariables(equinoxProperties, equinoxProperties);

        KarafCorePluginUtils.save(new File(getConfigDir(configuration), ECLIPSE_CONFIG_INI_FILE), equinoxProperties);
    }

    /**
     * Fixes the {@code karaf.jar} classpath entry so that it is compatible with
     * all versions of Eclipse.
     * <p>
     * This JAR ordinarily contains classes in the {@code org/osgi} package.
     * These classes conflict with Eclipse Indigo (3.7) and later versions of
     * the same classes found in the org.eclipse.osgi bundle.
     *
     * @param classpath
     *            the list of classpath entries to process
     * @return the list of classpath entries after being fixed to work in all
     *         versions of Eclipse
     * @throws CoreException
     *             if there is a problem fixing the classpath
     */
    private List<String> fixKarafJarClasspathEntry(final List<String> classpath)
            throws CoreException
    {
        final List<String> karafModelClasspath = karafPlatform.getBootClasspath();

        File karafJar = null;
        File filteredKarafJar = null;

        final Iterator<String> itr = karafModelClasspath.iterator();
        while (itr.hasNext()) {
            final String classpathEntry = itr.next();
            karafJar = new File(classpathEntry);
            if (!karafJar.getName().equalsIgnoreCase("karaf.jar")) {
                continue;
            }

            itr.remove();

            // TODO: This should be factored out somehow
            final IKarafProject karafProject = (IKarafProject) karafPlatform.getAdapter(IKarafProject.class);
            final IFile file = karafProject.getFile(".bin/runtime/generatedKaraf.jar");
            final IPath path = file.getRawLocation();
            filteredKarafJar = path.toFile();
        }

        if (filteredKarafJar != null) {
            classpath.add(filteredKarafJar.getAbsolutePath());
        } else {
            classpath.add(karafJar.getAbsolutePath());
        }
        return karafModelClasspath;
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

    private String mergeDeployedBundles(final String currentBundles, final ILaunchConfiguration configuration) throws CoreException {

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
                f.getWorkbenchService().getAdditionalBundles(workingKarafPlatform, configuration);

            for(final BundleEntry b : extBundles) {
                if(!deployedBundles.containsKey(b.getBundle())) {
                    deployedBundles.put(b.getBundle(), b);
                }
            }
        }

        return KarafCorePluginUtils.join(deployedBundles.values(), ",");  //$NON-NLS-1$
    }

}
