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
package org.apache.karaf.eik.felix;

import org.apache.karaf.eik.felix.internal.BundleEntry;
import org.apache.karaf.eik.felix.internal.FelixLaunchHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.osgi.framework.Bundle;

public class FelixLaunchConfiguration extends AbstractPDELaunchConfiguration {

    public static final String FELIX_FRAMEWORK_LAUNCHER = "org.apache.felix.main";

    public static final String FELIX_FRAMEWORK = "org.apache.felix.framework";

    public static final String FELIX_SYSTEM_PROPERTIES_KEY = "felix.system.properties";

    public static final String FELIX_CONFIG_PROPERTIES_KEY = "felix.config.properties";

    public static final String FELIX_AUTO_START_PREFIX = "felix.auto.start.";

    public static final String FELIX_AUTO_INSTALL_PREFIX = "felix.auto.install.";

    public static final String FELIX_CONFIG_PROPERTIES_FILE = "config.properties";

    public static final String FELIX_SYSTEM_PROPERTIES_FILE = "system.properties";

    public static final int DEFAULT_START_LEVEL = 4;

    public static final boolean DEFAULT_AUTOSTART = true;

    /**
     * Implementation of join using a {@link StringBuffer}
     *
     * @param items
     *            the {@link Collection} of items that will be joined together
     * @param glue
     *            the string to act as glue in the concatenation
     * @return the concatenation of the specified items
     */
    public static String join(Collection<String> items, String glue) {
        final StringBuffer buffer = new StringBuffer();
        for (String s : items) {
            if (buffer.length() > 0) {
                buffer.append(glue);
            }

            buffer.append(s);
        }

        return buffer.toString();
    }

    /**
     * Gets the path to the specified bundle in the following manner:<br>
     * <br>
     * <ol>
     * <li>If the bundle is found in the Plug-in Registry and is not a workspace
     * resource, return the path to the bundle</li>
     * <li>If the bundle is in the Plug-in Registry but is a workspace resource,
     * return the path to the path to the output location that contains the
     * package specified ({@code project/output folder})</li>
     * <li>If the bundle is not found in the Plug-in Registry then look for it
     * in the OSGi platform</li>
     * </ol>
     *
     * @param bundleName
     *            the symbolic name of the bundle
     * @param packageName
     *            the name of the package used to locate the output folder
     * @return a fully qualified path to the requested bundle or null if it does
     *         not exist
     * @throws CoreException
     */
    private static String getBundlePath(String bundleName, String packageName) throws CoreException {
        final IPluginModelBase model = PluginRegistry.findModel(bundleName);
        if (model != null) {
            final IResource resource = model.getUnderlyingResource();

            if (!isWorkspaceModel(model)) {
                return model.getInstallLocation();
            }

            final IProject project = resource.getProject();
            if (project.hasNature(JavaCore.NATURE_ID)) {
                final IJavaProject jProject = JavaCore.create(project);
                final IClasspathEntry[] entries = jProject.getRawClasspath();

                for (int i = 0; i < entries.length; i++) {
                    final int kind = entries[i].getEntryKind();
                    if (kind == IClasspathEntry.CPE_SOURCE || kind == IClasspathEntry.CPE_LIBRARY) {
                        final IPackageFragmentRoot[] roots = jProject.findPackageFragmentRoots(entries[i]);

                        for (int j = 0; j < roots.length; j++) {
                            if (roots[j].getPackageFragment(packageName).exists()) {
                                // if source folder, find the output folder
                                if (kind == IClasspathEntry.CPE_SOURCE) {
                                    IPath path = entries[i].getOutputLocation();
                                    if (path == null) {
                                        path = jProject.getOutputLocation();
                                    }

                                    path = path.removeFirstSegments(1);

                                    return project.getLocation().append(path).toOSString();
                                }
                                // else if is a library jar, then get the
                                // location of the jar itself
                                final IResource jar = roots[j].getResource();
                                if (jar != null) {
                                    return jar.getLocation().toOSString();
                                }
                            }
                        }
                    }
                }
            }
        }

        final Bundle bundle = Platform.getBundle(bundleName);
        if (bundle != null) {
            try {
                URL url = FileLocator.resolve(bundle.getEntry("/")); //$NON-NLS-1$
                url = FileLocator.toFileURL(url);
                String path = url.getFile();
                if (path.startsWith("file:")) { //$NON-NLS-1$
                    path = path.substring(5);
                }

                path = new File(path).getAbsolutePath();

                if (path.endsWith("!")) { //$NON-NLS-1$
                    path = path.substring(0, path.length() - 1);
                }

                return path;
            } catch (IOException e) {
            }
        }

        return null;
    }

    /**
     * Uses the Eclipse Variables plugin to perform string substitution on the
     * input string
     *
     * @param text
     *            the input string that potentially contains variables that need
     *            to be resolved
     * @return the fully interpolated string
     * @throws CoreException
     */
    private static String getSubstitutedString(String text) throws CoreException {
        if (text == null) {
            return ""; //$NON-NLS-1$
        }

        final IStringVariableManager mgr = VariablesPlugin.getDefault().getStringVariableManager();
        return mgr.performStringSubstitution(text);
    }

    /**
     * Determines if the {@link IPluginModelBase} is a workspace resource
     *
     * @param model
     *            the {@code IPluginModelBase} to test
     * @return true if it is a workspace resource, false otherwise
     */
    private static boolean isWorkspaceModel(IPluginModelBase model) {
        return (model.getUnderlyingResource() != null);
    }

    /**
     * Helper method to write a properties file to the specified filename
     *
     * @param filename
     *            the fully qualfied filename
     * @param props
     *            the {@link Properties} to be stored
     * @throws CoreException
     */
    private static void writeProperties(String filename, Properties props) throws CoreException {
        try {
            final OutputStream out = new FileOutputStream(new File(filename));
            props.store(out, FelixLaunchConfiguration.class.getName());
            out.close();
        } catch (IOException e) {

        }
    }

    // used to generate the dev classpath entries
    // key is bundle ID, value is a model
    protected Map<String, IPluginModelBase> fAllBundles;

    // key is a model, value is startLevel:autoStart
    private Map<IPluginModelBase, String> fModels;

    /*
     * Apache Felix requires that the launcher and the Felix Framework be on the
     * initial classpath.
     *
     * @see
     * org.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration#getClasspath
     * (org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
        final String felixLauncherPath = getBundlePath(FELIX_FRAMEWORK_LAUNCHER, FELIX_FRAMEWORK_LAUNCHER);
        if (felixLauncherPath == null) {

        }

        final String felixFrameworkPath = getBundlePath(FELIX_FRAMEWORK, FELIX_FRAMEWORK);
        if (felixFrameworkPath == null) {

        }

        final List<String> entries = new ArrayList<String>();
        entries.add(felixLauncherPath);
        entries.add(felixFrameworkPath);

        /*
         * Add any boostrap entries provided by the user
         */
        final String bootstrap = configuration.getAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, ""); //$NON-NLS-1$
        final StringTokenizer tok = new StringTokenizer(getSubstitutedString(bootstrap), ","); //$NON-NLS-1$
        while (tok.hasMoreTokens()) {
            entries.add(tok.nextToken().trim());
        }

        return entries.toArray(new String[entries.size()]);
    }

    @Override
    public String getMainClass() {
        return "org.apache.felix.main.Main"; //$NON-NLS-1$
    }

    /*
     * Gets any additional program arguments from the user and writes all Felix
     * configuration files
     *
     * @seeorg.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration#
     * getProgramArguments(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String[] getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
        String args = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String) null);

        args = args == null ? "" : getSubstitutedString(args); //$NON-NLS-1$

        writeConfigProperties(configuration);
        writeSystemProperties(configuration);
        writeStartupProperties(configuration);

        return new ExecutionArguments("", args).getProgramArgumentsArray(); //$NON-NLS-1$
    }

    /*
     * Adds the base VM arguments and root system properties used to load the
     * Felix configuration files.
     *
     * @see
     * org.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration#getVMArguments
     * (org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String[] getVMArguments(ILaunchConfiguration configuration) throws CoreException {
        String args = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String) null);
        args = args == null ? "" : getSubstitutedString(args); //$NON-NLS-1$

        final IPath configProperties = new Path(new File(getConfigDir(configuration), FELIX_CONFIG_PROPERTIES_FILE).getAbsolutePath());
        final IPath systemProperties = new Path(new File(getConfigDir(configuration), FELIX_SYSTEM_PROPERTIES_FILE).getAbsolutePath());

        final String[] vmArgs = new ExecutionArguments("", args).getVMArgumentsArray(); //$NON-NLS-1$
        
        // arraycopy works for JDK 5 & 6
        final String[] newVmArgs = new String[vmArgs.length + 2];
        System.arraycopy(vmArgs, 0, newVmArgs, 0, vmArgs.length);
        
        try {
            // TODO: Properly merge arguments in
            newVmArgs[vmArgs.length] = "-D" + FELIX_CONFIG_PROPERTIES_KEY + "=" + configProperties.toFile().toURI().toURL();
            newVmArgs[vmArgs.length + 1] = "-D" + FELIX_SYSTEM_PROPERTIES_KEY + "=" + systemProperties.toFile().toURI().toURL();
        } catch (MalformedURLException e) {

        }

        return newVmArgs;
    }

    /**
     * Writes out the configuration properties including bundle start and
     * install values
     *
     * @param configuration
     *            the launch configuration
     * @throws CoreException
     */
    private void writeConfigProperties(ILaunchConfiguration configuration) throws CoreException {
        final File f = new File(getConfigDir(configuration), FELIX_CONFIG_PROPERTIES_FILE);

        final boolean defaultAutostart = configuration.getAttribute(IPDELauncherConstants.DEFAULT_AUTO_START, DEFAULT_AUTOSTART);
        final int defaultStart = configuration.getAttribute(IPDELauncherConstants.DEFAULT_START_LEVEL, DEFAULT_START_LEVEL);

        final Properties properties = new Properties();

        properties.put("osgi.bundles.defaultStartLevel", Integer.toString(defaultStart)); //$NON-NLS-1$
        properties.put("org.osgi.framework.startlevel.beginning", Integer.toString(defaultStart)); //$NON-NLS-1$

        final Map<String, List<String>> startEntries = new HashMap<String, List<String>>();
        final Map<String, List<String>> installEntries = new HashMap<String, List<String>>();

        for (IPluginModelBase model : fModels.keySet()) {
            final String startString = fModels.get(model);

            final int colonPos = startString.indexOf(":");

            Integer startLevel;
            String s = startString.substring(0, colonPos);
            if ("default".equals(s)) {
                startLevel = defaultStart;
            } else {
                startLevel = new Integer(s);
            }

            boolean autostart;
            s = startString.substring(colonPos + 1);
            if ("default".equals(s)) {
                autostart = defaultAutostart;
            } else {
                autostart = new Boolean(s);
            }

            List<String> entries;
            if (autostart) {
                entries = startEntries.get(FELIX_AUTO_START_PREFIX + startLevel.toString());
                if (entries == null) {
                    entries = new ArrayList<String>();
                    startEntries.put(FELIX_AUTO_START_PREFIX + startLevel.toString(), entries);
                }
            } else {
                entries = startEntries.get(FELIX_AUTO_INSTALL_PREFIX + startLevel.toString());
                if (entries == null) {
                    entries = new ArrayList<String>();
                    installEntries.put(FELIX_AUTO_INSTALL_PREFIX + startLevel.toString(), entries);
                }
            }

            try {
                final File bundle = new File(model.getBundleDescription().getLocation());
                entries.add(bundle.toURI().toURL().toString());
            } catch (MalformedURLException e) {
            }
        }

        for (Map.Entry<String, List<String>> e : startEntries.entrySet()) {
            properties.put(e.getKey(), join(e.getValue(), " "));
        }

        for (Map.Entry<String, List<String>> e : installEntries.entrySet()) {
            properties.put(e.getKey(), join(e.getValue(), " "));
        }

        writeProperties(f.getAbsolutePath(), properties);
    }

    private void writeStartupProperties(ILaunchConfiguration configuration) throws CoreException {
        final File f = new File(getConfigDir(configuration), "startup.properties"); //$NON-NLS-1$

        final Properties properties = new Properties();

        writeProperties(f.getAbsolutePath(), properties);
    }

    private void writeSystemProperties(ILaunchConfiguration configuration) throws CoreException {
        final File f = new File(getConfigDir(configuration), FELIX_SYSTEM_PROPERTIES_FILE);

        final Properties properties = new Properties();

        writeProperties(f.getAbsolutePath(), properties);
    }

    /*
     * Sets up the initial map of bundles.
     *
     * @see
     * org.eclipse.pde.ui.launcher.AbstractPDELaunchConfiguration#preLaunchCheck
     * (org.eclipse.debug.core.ILaunchConfiguration,
     * org.eclipse.debug.core.ILaunch,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void preLaunchCheck(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        final String workspace = configuration.getAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES, ""); //$NON-NLS-1$
        final String target = configuration.getAttribute(IPDELauncherConstants.TARGET_BUNDLES, ""); //$NON-NLS-1$

        final List<BundleEntry> workspaceBundles = FelixLaunchHelper.getBundles(workspace);
        final List<BundleEntry> targetBundles = FelixLaunchHelper.getBundles(target);

        fModels = new HashMap<IPluginModelBase, String>();
        for(BundleEntry e : workspaceBundles) {
            final IPluginModelBase model = FelixLaunchHelper.resolveWorkspaceBundleEntry(e);
            if(model == null) {
                continue;
            }

            fModels.put(model, e.getAutostart() + ":" + e.getStartLevel()); //$NON-NLS-1$
        }

        if (configuration.getAttribute(IPDELauncherConstants.AUTOMATIC_ADD, true)) {
            automaticallyAddWorkspaceBundles(configuration);
        }

        for(BundleEntry e : targetBundles) {
            final IPluginModelBase model = FelixLaunchHelper.resolveTargetBundleEntry(e);
            if(model == null) {
                continue;
            }

            fModels.put(model, e.getAutostart() + ":" + e.getStartLevel()); //$NON-NLS-1$
        }

        fAllBundles = new HashMap<String, IPluginModelBase>(fModels.size());

        final Iterator<IPluginModelBase> iter = fModels.keySet().iterator();
        while (iter.hasNext()) {
            final IPluginModelBase model = iter.next();
            fAllBundles.put(model.getPluginBase().getId(), model);
        }

        super.preLaunchCheck(configuration, launch, monitor);
    }

    private void automaticallyAddWorkspaceBundles(ILaunchConfiguration configuration) throws CoreException {
        final Set<IPluginModelBase> deselectedPlugins = FelixLaunchHelper.getDeselectedPluginSet(configuration);

        final IPluginModelBase[] models = PluginRegistry.getWorkspaceModels();
        for (int i = 0; i < models.length; i++) {
            final String id = models[i].getPluginBase().getId();
            if (id == null) {
                continue;
            }

            if (!deselectedPlugins.contains(models[i])) {
                if (!fModels.containsKey(models[i])) {
                    fModels.put(models[i], "default:default"); //$NON-NLS-1$
                }
            }
        }
    }

}
