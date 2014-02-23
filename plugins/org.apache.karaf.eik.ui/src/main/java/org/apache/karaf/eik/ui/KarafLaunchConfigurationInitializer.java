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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.eik.core.IKarafConstants;
import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelFactory;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.core.SystemBundleNames;
import org.apache.karaf.eik.core.configuration.StartupSection;
import org.apache.karaf.eik.core.equinox.BundleEntry;
import org.apache.karaf.eik.core.model.WorkingKarafPlatformModel;
import org.apache.karaf.eik.ui.features.FeaturesBundlesStartLevels;
import org.apache.karaf.eik.ui.internal.WorkbenchServiceExtensions;
import org.apache.karaf.eik.ui.project.KarafProject;
import org.apache.karaf.eik.ui.workbench.KarafWorkbenchServiceFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.launching.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.internal.ui.IPDEUIConstants;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.pde.launching.OSGiLaunchConfigurationInitializer;

@SuppressWarnings("restriction")
public class KarafLaunchConfigurationInitializer extends OSGiLaunchConfigurationInitializer {

    public static final char VERSION_SEPARATOR = '*';

    /**
     * Convenience method for initializing a Karaf launch configuration
     *
     * @param configuration the working copy of the launch configuration
     */
    public static void initializeConfiguration(final ILaunchConfigurationWorkingCopy configuration) {
        final KarafLaunchConfigurationInitializer configurationInitializer = new KarafLaunchConfigurationInitializer();
        configurationInitializer.initialize(configuration);
    }

    /**
     * The model that represents the Karaf platform
     */
    protected KarafPlatformModel karafPlatform;

    protected KarafPlatformModelFactory karafPlatformFactory;

    protected StartupSection startupSection;

    private FeaturesBundlesStartLevels featuresBundlesStartLevels;

    @Override
    public void initialize(final ILaunchConfigurationWorkingCopy configuration) {
        loadKarafPlatform(configuration);

        final File configDir = LaunchConfigurationHelper.getConfigurationArea(configuration);

        final IPath workingArea = new Path(configDir.getAbsolutePath());
        final WorkingKarafPlatformModel workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        workingKarafPlatform.getConfigurationDirectory().toFile().mkdirs();
        workingKarafPlatform.getUserDeployedDirectory().toFile().mkdirs();

        // TODO: Factor this out so that it pulls the ID from this plugins
        // registry
        configuration.setAttribute(IPDELauncherConstants.OSGI_FRAMEWORK_ID, "org.apache.karaf.eik.Framework"); //$NON-NLS-1$
        configuration.setAttribute(IPDEUIConstants.LAUNCHER_PDE_VERSION, "3.3"); //$NON-NLS-1$

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_SOURCE_RUNTIME, karafPlatform.getRootDirectory().toOSString());

        addDefaultVMArguments(configuration);

        try {
            final List<KarafWorkbenchServiceFactory> list = WorkbenchServiceExtensions.getLaunchCustomizerFactories();

            for (final KarafWorkbenchServiceFactory f : list) {
                f.getWorkbenchService().initialize(workingKarafPlatform, configuration);
            }
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to access extension registry", e);
        }

        // This must be the last item called
        super.initialize(configuration);
    }

    /**
     * Initializes the auto start property to true for the bundles found in the
     * Karaf platform and defers the the parent for all other bundles.
     *
     * @see org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer#getAutoStart
     * (java.lang.String)
     */
    @Override
    protected String getAutoStart(final String bundleID) {
        if (startupSection.containsPlugin(bundleID)) {
            return "true"; //$NON-NLS-1$
        } else if (featuresBundlesStartLevels.containsPlugin(bundleID)) {
            return "true"; //$NON-NLS-1$
        } else {
            return super.getAutoStart(bundleID);
        }
    }

    /**
     * Initializes the bundles associated with the Karaf platform with the
     * correct default start levels and falls back to the default start level
     * provided by the parent.
     *
     * @see org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer#getStartLevel
     * (java.lang.String)
     */
    @Override
    protected String getStartLevel(final String bundleID) {
        if (startupSection.containsPlugin(bundleID)) {
            return startupSection.getStartLevel(bundleID);
        } else if (featuresBundlesStartLevels.containsPlugin(bundleID)) {
            return featuresBundlesStartLevels.getStartLevel(bundleID);
        } else {
            return super.getStartLevel(bundleID);
        }
    }

    @Override
    protected void initializeBundleState(final ILaunchConfigurationWorkingCopy configuration) {
        super.initializeBundleState(configuration);

        final List<String> externalPlugins = new ArrayList<String>();
        final List<String> workspacePlugins = new ArrayList<String>();

        final IPluginModelBase[] models = PluginRegistry.getActiveModels();
        for (int i = 0; i < models.length; i++) {
            final String id = models[i].getPluginBase().getId();

            // Skip the Felix OSGi Framework
            if (SystemBundleNames.FELIX.toString().equals(id)) {
                continue;
            }

            final BundleEntry entry = new BundleEntry.Builder(getBundleId(models[i])).autostart(getAutoStart(id)).startLevel(getStartLevel(id)).build();

            final boolean inWorkspace = models[i].getUnderlyingResource() != null;
            if (inWorkspace) {
                workspacePlugins.add(entry.toString());
            } else {
                externalPlugins.add(entry.toString());
            }
        }

        configuration.setAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES, KarafCorePluginUtils.join(workspacePlugins, ","));
        configuration.setAttribute(IPDELauncherConstants.TARGET_BUNDLES, KarafCorePluginUtils.join(externalPlugins, ","));
        configuration.setAttribute(IPDELauncherConstants.AUTOMATIC_ADD, true);
        configuration.setAttribute(IPDELauncherConstants.SHOW_SELECTED_ONLY, true);
    }

    /**
     * Initializes a series of default configuration items for the framework
     * launcher. This includes registering the default boot classpath entries
     * and setting the Karaf platform default OSGi bundle start level.
     *
     * @param configuration the launch configuration
     * @see org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer#initializeFrameworkDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    @Override
    protected void initializeFrameworkDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        final List<String> bootClasspathEntries = karafPlatform.getBootClasspath();

        final String bootClasspath = KarafCorePluginUtils.join(bootClasspathEntries, ",");

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH, bootClasspath);

        configuration.setAttribute(IPDELauncherConstants.DEFAULT_START_LEVEL, Integer.parseInt(IKarafConstants.KARAF_DEFAULT_BUNDLE_START_LEVEL));
    }

    /**
     * Loads a Karaf platform definition based on the context of the launch
     * configuration.
     *
     * @param configuration
     */
    protected void loadKarafPlatform(final ILaunchConfigurationWorkingCopy configuration) {
        try {
            this.karafPlatform = KarafPlatformModelRegistry.findActivePlatformModel();

            if (karafPlatform == null) {
                throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Karaf target platform is not set !"));
            }

            this.karafPlatformFactory = KarafPlatformModelRegistry.findPlatformModelFactory(karafPlatform.getRootDirectory());

            this.startupSection = (StartupSection) this.karafPlatform.getAdapter(StartupSection.class);
            this.startupSection.load();

            IProject project = findProjectForActiveTargetPlatform();
            if (project != null) {
                this.featuresBundlesStartLevels = new FeaturesBundlesStartLevels(new KarafProject(project));
                this.featuresBundlesStartLevels.load();
            } else {
                throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Target platform is not set from Karaf project !"));
            }
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to locate the Karaf platform", e);

            this.karafPlatform = null;
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private IProject findProjectForActiveTargetPlatform() throws CoreException {
        ITargetPlatformService service = (ITargetPlatformService) PDECore.getDefault().acquireService(ITargetPlatformService.class.getName());

        ITargetHandle workspaceTargetHandle = service.getWorkspaceTargetHandle();
        if (workspaceTargetHandle != null && workspaceTargetHandle.exists()) {
            String memento = workspaceTargetHandle.getMemento();

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            return root.getProject(getProjectNameFromMemento(memento));
        } else {
            return null;
        }
    }

    private String getProjectNameFromMemento(String memento) {
        String[] split = memento.split("/");
        if (split.length != 3) {
            throw new IllegalArgumentException();
        } else {
            return split[1];
        }
    }

    /**
     * Returns the a plugin id favoring the newest version in the target
     * platform
     *
     * @param model the {@link IPluginModelBase}
     * @return the string plugin identifier with an optional version set at the
     * newest version if there is more than one plugin that responds to
     * the given id
     */
    private String getBundleId(final IPluginModelBase model) {
        final IPluginBase base = model.getPluginBase();
        final String id = base.getId();
        final StringBuffer buffer = new StringBuffer(id);

        final ModelEntry entry = PluginRegistry.findEntry(id);
        if (entry.getActiveModels().length > 1) {
            buffer.append(VERSION_SEPARATOR);
            buffer.append(model.getPluginBase().getVersion());
        }

        return buffer.toString();
    }

    /**
     * Adds default VM arguments to this launch configuration
     *
     * @param configuration the working copy of the launch configuration
     */
    private void addDefaultVMArguments(final ILaunchConfigurationWorkingCopy configuration) {
        final StringBuffer vmArgs = new StringBuffer();

        if (vmArgs.indexOf("-Dosgi.noShutdown") == -1) { //$NON-NLS-1$
            vmArgs.append(" -Dosgi.noShutdown=true"); //$NON-NLS-1$
        }

        // prevent terminal CTRL-characters in Eclipse console on Windows
        final String localOperatingSystem = System.getProperty("os.name"); //$NON-NLS-1$
        if (localOperatingSystem.toLowerCase().indexOf("windows") >= 0 //$NON-NLS-1$
                && vmArgs.indexOf("-Djline.terminal") == -1) { //$NON-NLS-1$
            vmArgs.append(" -Djline.terminal=jline.UnsupportedTerminal"); //$NON-NLS-1$
        }

        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs.toString());
    }

}
