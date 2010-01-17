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
import info.evanchik.eclipse.karaf.core.KarafModelSynchronizer;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.SystemBundleNames;
import info.evanchik.eclipse.karaf.core.configuration.StartupSection;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.core.model.BundleKarafPlatformModel;
import info.evanchik.eclipse.karaf.core.model.DefaultKarafModelSynchronizer;
import info.evanchik.eclipse.karaf.core.model.DirectoryKarafPlatformModel;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
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
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.ui.IPDEUIConstants;
import org.eclipse.pde.internal.ui.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafLaunchConfigurationInitializer extends OSGiLaunchConfigurationInitializer {

    /**
     * Plugin that implements low-level Equinox hooks for the Karaf Eclipse
     * integration
     */
    public static String KARAF_HOOK_PLUGIN_ID = "info.evanchik.eclipse.karaf.hooks"; //$NON-NLS-1$

    /**
     * Sub-directories found in Karaf distributions
     */
    public static String[] KARAF_SUB_DIRECTORIES = new String[] { "etc", "system" }; //$NON-NLS-1$ $NON-NLS-2$

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
    public static KarafPlatformModel findKarafPlatform(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
        IPluginModelBase karafPlatform = PluginRegistry.findModel(KarafPlatformModel.KARAF_MAIN_BUNDLE_SYMBOLIC_NAME);
        if (karafPlatform == null) {
            monitor.worked(10);
            karafPlatform = PluginRegistry.findModel(KarafPlatformModel.KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME);
        }

        monitor.worked(10);

        if (karafPlatform == null) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                    "Unable to locate Karaf Platform. Please use set the Target Platform to include a valid set of Karaf bundles"));
        }

        final IPath modelPath = new Path(karafPlatform.getInstallLocation());
        if (modelPath.toFile().isDirectory()) {
            // This is in the workspace! What to do?!
            throw new CoreException(new Status(IStatus.WARNING, KarafUIPluginActivator.PLUGIN_ID,
                    "The Karaf Target Platform cannot be projects in your workspace: " + modelPath));
        }

        // Strip the filename and its containing directory (lib/<filename>)
        final IPath rootPath = modelPath.removeLastSegments(2);

        // This must be Platform.getBundle() because the plugin may not be in
        // the Target Platform or Workspace
        final Bundle karafPluginBundle = Platform.getBundle(KarafPlatformModel.KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME);

        monitor.worked(10);

        final KarafPlatformModel karafPlatformModel;

        if (karafPluginBundle != null) {
            final File builtInKarafPlatform;
            try {
                builtInKarafPlatform = FileLocator.getBundleFile(karafPluginBundle);
            } catch (IOException e) {
                throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                        "Unable to resolve built in Karaf Target Platform to File", e));
            }

            if (!builtInKarafPlatform.isDirectory()) {
                throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID,
                        "Invalid built in Karaf Target Platform: The bundle must be unpacked"));
            }

            monitor.worked(10);

            // Remove the "runtimes/karaf" from the bundle path
            final IPath testBundlePath = rootPath.removeLastSegments(2);
            final IPath bundlePath = new Path(builtInKarafPlatform.getAbsolutePath());

            if (testBundlePath.equals(bundlePath)) {
                karafPlatformModel = new BundleKarafPlatformModel(karafPluginBundle);
            } else {
                karafPlatformModel = new DirectoryKarafPlatformModel(rootPath);
            }
        } else {
            karafPlatformModel = new DirectoryKarafPlatformModel(rootPath);
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
    public static void initializeConfiguration(ILaunchConfigurationWorkingCopy configuration) {
        final KarafLaunchConfigurationInitializer configurationInitializer = new KarafLaunchConfigurationInitializer();
        configurationInitializer.initialize(configuration);
    }

    /**
     * This will synchronize the Karaf Equinox hook provider to the target
     * platform if possible. Equinox hook providers must be in the same
     * directory as the JAR that provides OSGi (org.eclipse.osgi*.jar).
     *
     * @param karafPlatform
     *            the {@link KarafPlatformModel} that will be synchronized
     */
    public static void synchronizeHooksWithPlatform(KarafPlatformModel karafPlatform) {
        if (karafPlatform.isReadOnly()) {
            KarafUIPluginActivator.getLogger().info(
                    "Cannot synchronize a read-only target platform: " + karafPlatform.getRootDirectory().toOSString());
            return;
        }

        final BundleDescription hookIsPresent = karafPlatform.getState().getBundle(
                KarafLaunchConfigurationInitializer.KARAF_HOOK_PLUGIN_ID, null);

        if (hookIsPresent != null) {
            return;
        }

        final Bundle bundleKarafPlatform = Platform.getBundle(KarafPlatformModel.KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME);
        if (bundleKarafPlatform == null) {
            return;
        }

        final String wildcard = KARAF_HOOK_PLUGIN_ID + "*.jar";
        final Enumeration<?> enumeration = bundleKarafPlatform.findEntries(BundleKarafPlatformModel.KARAF_RUNTIME_BUNDLES_LOCATION,
                wildcard, false);

        if (enumeration == null || !enumeration.hasMoreElements()) {
            KarafUIPluginActivator.getLogger().error(
                    "Cannot discover platform model entries at " + BundleKarafPlatformModel.KARAF_RUNTIME_BUNDLES_LOCATION);
            return;
        }

        final URL hookBundleUrl = (URL) enumeration.nextElement();

        try {
            final URL fileSrc = FileLocator.toFileURL(hookBundleUrl);
            final File src = new File(fileSrc.toURI());

            /*
             * Build up the destination conceptually as <dir to Equinox JAR> /
             * <hook JAR filename>
             */
            final BundleDescription equinox = karafPlatform.getState().getBundle("org.eclipse.osgi", null);
            if (equinox == null) {
                // TODO: throw new CoreException()
                return;
            }

            IPath dstPath = new Path(equinox.getLocation());
            dstPath = dstPath.removeLastSegments(1);
            dstPath = dstPath.append(src.getName());

            KarafCorePluginUtils.copyFile(src, dstPath.toFile());

        } catch (IOException e) {
            KarafUIPluginActivator.getLogger().error("Unable to copy hook implementation from", e);
        } catch (URISyntaxException e) {
            KarafUIPluginActivator.getLogger().error("Could not resolve File URL to URL");
        }

    }

    /**
     * The model that represents the Karaf platform
     */
    protected KarafPlatformModel karafPlatform;

    protected StartupSection startupSection;

    @Override
    public void initialize(ILaunchConfigurationWorkingCopy configuration) {
        loadKarafPlatform(configuration);

        synchronizeHooksWithPlatform(this.karafPlatform);

        final File configDir = LaunchConfigurationHelper.getConfigurationArea(configuration);
        setupKarafDirectoryStructure(configDir);

        final IPath workingArea = new Path(configDir.getAbsolutePath());
        final KarafPlatformModel workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        final KarafModelSynchronizer synchronizer = DefaultKarafModelSynchronizer.getInstance();
        synchronizer.synchronize(karafPlatform, workingKarafPlatform, "java.util.logging.properties"); //$NON-NLS-1$
        synchronizer.synchronize(karafPlatform, workingKarafPlatform, "config.properties"); //$NON-NLS-1$
        synchronizer.synchronize(karafPlatform, workingKarafPlatform, "system.properties"); //$NON-NLS-1$
        synchronizer.synchronize(karafPlatform, workingKarafPlatform, "startup.properties"); //$NON-NLS-1$

        // TODO: Make this part of the configuration sections
        synchronizeDefaultConfigAdminPrefs(workingKarafPlatform);

        // TODO: Factor this out so that it pulls the ID from this plugins
        // registry
        configuration.setAttribute(IPDELauncherConstants.OSGI_FRAMEWORK_ID, "info.evanchik.eclipse.karaf.Framework"); //$NON-NLS-1$
        configuration.setAttribute(IPDEUIConstants.LAUNCHER_PDE_VERSION, "3.3"); //$NON-NLS-1$

        addDefaultVMArguments(configuration);

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
    protected String getAutoStart(String bundleID) {
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
    protected String getStartLevel(String bundleID) {
        if (startupSection.containsPlugin(bundleID)) {
            return startupSection.getStartLevel(bundleID);
        } else {
            return super.getStartLevel(bundleID);
        }
    }

    @Override
    protected void initializeBundleState(ILaunchConfigurationWorkingCopy configuration) {
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

            boolean inWorkspace = models[i].getUnderlyingResource() != null;
            if (inWorkspace) {
                workspacePlugins.add(entry.toString());
            } else {
                // By default, only add the plugin if it is in the Karaf model
                final Version v = Version.parseVersion(models[i].getPluginBase().getVersion());
                if (karafPlatform.getState().getBundle(id, v) != null) {
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
    protected void initializeFrameworkDefaults(ILaunchConfigurationWorkingCopy configuration) {
        final List<String> bootClasspathEntries = karafPlatform.getBootClasspath();

        final String bootClasspath = KarafCorePluginUtils.join(bootClasspathEntries, ",");

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH, bootClasspath);
        configuration.setAttribute(IPDELauncherConstants.DEFAULT_START_LEVEL, new Integer(
                KarafPlatformModel.KARAF_DEFAULT_BUNDLE_START_LEVEL));
    }

    /**
     * Loads a Karaf platform definition based on the context of the launch
     * configuration.
     *
     * @param configuration
     */
    protected void loadKarafPlatform(ILaunchConfigurationWorkingCopy configuration) {
        try {
            this.karafPlatform = findKarafPlatform(configuration, new NullProgressMonitor());

            this.startupSection = (StartupSection) Platform.getAdapterManager().getAdapter(this.karafPlatform, StartupSection.class);
            this.startupSection.load();
        } catch (CoreException e) {
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
    private String getBundleId(IPluginModelBase model) {
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
     * Sets up the proper Karaf directory structure in {@code karaf.base}:
     *
     * @param configuration
     *            the current launcher configuration used to locate the base
     *            configuration directory
     */
    private void setupKarafDirectoryStructure(File karafBaseDirectory) {
        for (String subdir : KARAF_SUB_DIRECTORIES) {
            final File dir = new File(karafBaseDirectory, subdir);
            dir.mkdirs();
        }
    }

    /**
     * Adds default VM arguments to this launch configuration
     *
     * @param configuration
     *            the working copy of the launch configuration
     */
    private void addDefaultVMArguments(ILaunchConfigurationWorkingCopy configuration) {
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

        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs.toString());

    }

    /**
     * Copy all of the default OSGi Config Admin prefs files to the launch
     * configuration directory. This method will not overwrite an existing
     * configuration file if it exists.
     *
     * @param workingKarafPlatform
     *            the working Karaf platform configuration
     */
    private void synchronizeDefaultConfigAdminPrefs(KarafPlatformModel workingKarafPlatform) {
        final File runtimeConfigDir = karafPlatform.getConfigurationDirectory().toFile();

        for (File srcConfigFile : runtimeConfigDir.listFiles()) {
            // Config Admin files are *.cfg
            if (!srcConfigFile.getAbsolutePath().endsWith(".cfg")) { //$NON-NLS-1$
                continue;
            }

            final File dstConfigFile = workingKarafPlatform.getConfigurationDirectory().append(srcConfigFile.getName()).toFile();
            if (dstConfigFile.exists()) {
                continue;
            }

            // The file does not exist so load the properties file and write it
            // out to the destination. This could change to a straight file
            // copy.
            try {
                final Properties props = new Properties();
                final InputStream in = new FileInputStream(srcConfigFile);

                props.load(in);

                in.close();

                LaunchConfigurationHelper.save(dstConfigFile, props);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
