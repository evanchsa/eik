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
package info.evanchik.eclipse.smk.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelWorkbenchService implements KarafWorkbenchService {

    public List<BundleEntry> getAdditionalBundles(KarafWorkingPlatformModel platformModel) {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyList();
        }

        String[] bundles = {
                "info.evanchik.smk.app",
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

        for (String b : bundles) {
            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(b);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    public Map<String, String> getAdditionalEquinoxConfiguration(KarafWorkingPlatformModel platformModel) {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyMap();
        }

        final Map<String, String> equinoxProperties = new HashMap<String, String>();

        equinoxProperties.put(
                "servicemix.base",
                platformModel.getRootDirectory().toOSString());

        equinoxProperties.put(
                "servicemix.home",
                platformModel.getRootDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.configDir",
                platformModel.getConfigurationDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.monitorDir",
                platformModel.getUserDeployedDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.generatedJarDir",
                platformModel.getRootDirectory() + "/data/generated-bundles");

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.scanInterval",
                "500");

        equinoxProperties.put(
                "eclipse.application",
                "info.evanchik.smk.app.servicemix.kernel");

        equinoxProperties.put(
                "osgi.resolver.usesMode",
                "ignore");

        equinoxProperties.put(
                "org.osgi.framework.bootdelegation",
                "sun.*,com.sun.management.*");

        try {
            final Properties currentConfig =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getConfigurationDirectory().toFile(),
                    KarafPlatformModel.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE);

            final String extraSystemPackages = "org.osgi.framework;version=\"1.4.0\",org.apache.servicemix.kernel.main.spi;version=\"1.0.0\",org.apache.servicemix.kernel.jaas.boot,org.apache.servicemix.kernel.version".concat(",").concat(currentConfig.getProperty("jre-1.6"));
            equinoxProperties.put(
                    "org.osgi.framework.system.packages.extra",
                    extraSystemPackages);
        } catch(CoreException e) {

        }

        return equinoxProperties;
    }

    public List<String> getVMArguments(
            KarafWorkingPlatformModel platformModel,
            ILaunchConfiguration configuration) throws CoreException
    {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyList();
        }

        final List<String> arguments = new ArrayList<String>();

        arguments.add("-Dservicemix.home=" + platformModel.getRootDirectory());
        arguments.add("-Dservicemix.base=" + platformModel.getRootDirectory());

        arguments.add("-Dservicemix.startLocalConsole=true");
        arguments.add("-Dservicemix.startRemoteShell=true");

        arguments.add("-Djava.util.logging.config.file=" + platformModel.getConfigurationDirectory() + "/java.util.logging.properties");

        return arguments;
    }

    public void launch(
            KarafWorkingPlatformModel platformModel,
            ILaunchConfiguration configuration,
            String mode,
            ILaunch launch,
            IProgressMonitor monitor) throws CoreException
    {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return;
        }
    }

}
