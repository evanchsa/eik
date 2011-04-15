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

import info.evanchik.eclipse.karaf.core.IKarafConstants;
import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelFactory;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelSynchronizer;
import info.evanchik.eclipse.karaf.core.SystemBundleNames;
import info.evanchik.eclipse.karaf.core.configuration.StartupSection;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.internal.WorkbenchServiceExtensions;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.launching.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.internal.ui.IPDEUIConstants;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.pde.launching.OSGiLaunchConfigurationInitializer;
import org.osgi.framework.Version;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafLaunchConfigurationInitializer extends OSGiLaunchConfigurationInitializer {

    public static final char VERSION_SEPARATOR = '*';

    /**
     * Loads the Karaf platform in the following manner:<br>
     * <br>
     * <ol>
     * <li>Locate a suitable reference platform bundle either:
     * {@link KarafPlatformModel.KARAF_MAIN_BUNDLE_SYMBOLIC_NAME} or
     * {@link KarafPlatformModel.KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME}</li>
     * <li>Locate the built in Karaf platform bundle:
     * {@link KarafPlatformModel.KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME}</li>
     * <li>If the built in Karaf platform bundle does not exist, assume an
     * external installation and construct a path to its root directory</li>
     * <li>If the built in Karaf platform bundle exists compare its root
     * location to the location of the reference platform bundle found in step
     * 1. If they match, use the built in Karaf plugin</li>
     * <li>Finally, if there is no match, assume an external Karaf installation
     * and construct the root path accordingly</li>
     * </ol>
     *
     * @param configuration
     * @param monitor
     * @throws CoreException
     */
    public static KarafPlatformModel findKarafPlatform(final ILaunchConfiguration configuration, final IProgressMonitor monitor) throws CoreException {
    	monitor.subTask("Locating Karaf Platform");

    	final KarafPlatformModel karafPlatformModel =
            KarafPlatformModelRegistry.findActivePlatformModel();

        monitor.worked(10);

        if (karafPlatformModel == null) {
            throw new CoreException(
                    new Status(
                            IStatus.ERROR,
                            KarafUIPluginActivator.PLUGIN_ID,
                            "Unable to locate compatible Apache Felix Karaf platform model"));
        }

        monitor.worked(10);

        return karafPlatformModel;
    }

    /**
     * Convenience method for initializing a Karaf launch configuration
     *
     * @param configuration
     *            the working copy of the launch configuration
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

    @Override
    public void initialize(final ILaunchConfigurationWorkingCopy configuration) {
        loadKarafPlatform(configuration);

        final File configDir =
            LaunchConfigurationHelper.getConfigurationArea(configuration);

        final IPath workingArea = new Path(configDir.getAbsolutePath());
        final WorkingKarafPlatformModel workingKarafPlatform =
            new WorkingKarafPlatformModel(workingArea, karafPlatform);

        workingKarafPlatform.getConfigurationDirectory().toFile().mkdirs();
        workingKarafPlatform.getUserDeployedDirectory().toFile().mkdirs();

        final KarafPlatformModelSynchronizer synchronizer =
            karafPlatformFactory.getPlatformSynchronizer(karafPlatform);

        synchronizer.synchronize(workingKarafPlatform, true);

        // TODO: Factor this out so that it pulls the ID from this plugins
        // registry
        configuration.setAttribute(IPDELauncherConstants.OSGI_FRAMEWORK_ID, "info.evanchik.eclipse.karaf.Framework"); //$NON-NLS-1$
        configuration.setAttribute(IPDEUIConstants.LAUNCHER_PDE_VERSION, "3.3"); //$NON-NLS-1$

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
     *      (java.lang.String)
     */
    @Override
    protected String getAutoStart(final String bundleID) {
        if (startupSection.containsPlugin(bundleID)) {
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
     *      (java.lang.String)
     */
    @Override
    protected String getStartLevel(final String bundleID) {
        if (startupSection.containsPlugin(bundleID)) {
            return startupSection.getStartLevel(bundleID);
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

            final BundleEntry entry = new BundleEntry.Builder(getBundleId(models[i])).autostart(getAutoStart(id)).startLevel(
                    getStartLevel(id)).build();

            final boolean inWorkspace = models[i].getUnderlyingResource() != null;
            if (inWorkspace) {
                workspacePlugins.add(entry.toString());
            } else {
                // By default, only add the plugin if it is in the Karaf model
                final Version v = Version.parseVersion(models[i].getPluginBase().getVersion());
                if (karafPlatform.getState().getBundle(id, v) != null && startupSection.containsPlugin(id)) {
                    externalPlugins.add(entry.toString());
                }
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
     * @param configuration
     *            the launch configuration
     * @see org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer#initializeFrameworkDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    @Override
    protected void initializeFrameworkDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        final List<String> bootClasspathEntries = karafPlatform.getBootClasspath();

        final String bootClasspath = KarafCorePluginUtils.join(bootClasspathEntries, ",");

        configuration.setAttribute(
                KarafLaunchConfigurationConstants.KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH,
                bootClasspath);

        configuration.setAttribute(
                IPDELauncherConstants.DEFAULT_START_LEVEL,
                Integer.parseInt(IKarafConstants.KARAF_DEFAULT_BUNDLE_START_LEVEL));
    }

    /**
     * Loads a Karaf platform definition based on the context of the launch
     * configuration.
     *
     * @param configuration
     */
    protected void loadKarafPlatform(final ILaunchConfigurationWorkingCopy configuration) {
        try {
            this.karafPlatform = findKarafPlatform(configuration, new NullProgressMonitor());
            this.karafPlatformFactory = KarafPlatformModelRegistry.findPlatformModelFactory(karafPlatform.getRootDirectory());

            this.startupSection = (StartupSection) Platform.getAdapterManager().getAdapter(this.karafPlatform, StartupSection.class);
            this.startupSection.load();
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to locate the Karaf platform", e);

            this.karafPlatform = null;
        }
    }

    /**
     * Returns the a plugin id favoring the newest version in the target
     * platform
     *
     * @param model
     *            the {@link IPluginModelBase}
     * @return the string plugin identifier with an optional version set at the
     *         newest version if there is more than one plugin that responds to
     *         the given id
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
     * @param configuration
     *            the working copy of the launch configuration
     */
    private void addDefaultVMArguments(final ILaunchConfigurationWorkingCopy configuration) {
        final StringBuffer vmArgs = new StringBuffer();
        if (vmArgs.indexOf("-Declipse.ignoreApp") == -1) { //$NON-NLS-1$
            if (vmArgs.length() > 0) {
                vmArgs.append(" "); //$NON-NLS-1$
            }
            vmArgs.append("-Declipse.ignoreApp=true"); //$NON-NLS-1$
        }

        if (vmArgs.indexOf("-Dosgi.noShutdown") == -1) { //$NON-NLS-1$
            vmArgs.append(" -Dosgi.noShutdown=true"); //$NON-NLS-1$
        }

        // prevent terminal CTRL-characters in Eclipse console on Windows
        final String localOperatingSystem = System.getProperty("os.name"); //$NON-NLS-1$
        if (   localOperatingSystem.toLowerCase().indexOf("windows") >= 0 //$NON-NLS-1$
            && vmArgs.indexOf("-Djline.terminal") == -1) { //$NON-NLS-1$
            vmArgs.append(" -Djline.terminal=jline.UnsupportedTerminal"); //$NON-NLS-1$
        }

        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs.toString());

    }
}
