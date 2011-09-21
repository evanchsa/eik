/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.smk.internal;

import org.apache.karaf.eclipse.core.IKarafConstants;
import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafWorkingPlatformModel;
import org.apache.karaf.eclipse.core.equinox.BundleEntry;
import org.apache.karaf.eclipse.ui.workbench.KarafWorkbenchService;

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
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelWorkbenchService implements KarafWorkbenchService {

    @Override
    public List<BundleEntry> getAdditionalBundles(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyList();
        }

        final String[] bundles = {
                "org.apache.karaf.eclipse.app.smk",
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
            final String bundleLocation =
                KarafCorePluginUtils.getBundleLocation(b);

            final BundleEntry entry =
                new BundleEntry.Builder(bundleLocation).startLevel("1").autostart("start").build(); //$NON-NLS-1$ $NON-NLS-2$

            bundleEntries.add(entry);
        }

        return bundleEntries;
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(final KarafWorkingPlatformModel platformModel, final ILaunchConfiguration configuration) {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyMap();
        }

        final Map<String, String> equinoxProperties = new HashMap<String, String>();

        equinoxProperties.put(
                "servicemix.base",
                platformModel.getParentKarafModel().getRootDirectory().toOSString());

        equinoxProperties.put(
                "servicemix.home",
                platformModel.getParentKarafModel().getRootDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.configDir",
                platformModel.getParentKarafModel().getConfigurationDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.monitorDir",
                platformModel.getParentKarafModel().getUserDeployedDirectory().toOSString());

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.generatedJarDir",
                platformModel.getRootDirectory() + "/data/generated-bundles");

        equinoxProperties.put(
                "org.apache.servicemix.filemonitor.scanInterval",
                "500");

        equinoxProperties.put(
                "eclipse.application",
                "org.apache.karaf.eclipse.app.smk.servicemix.kernel");

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
                    IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE);

            final String extraSystemPackages = "org.osgi.framework;version=\"1.4.0\",org.apache.servicemix.kernel.main.spi;version=\"1.0.0\",org.apache.servicemix.kernel.jaas.boot,org.apache.servicemix.kernel.version".concat(",").concat(currentConfig.getProperty("jre-1.6"));
            equinoxProperties.put(
                    "org.osgi.framework.system.packages.extra",
                    extraSystemPackages);

        } catch(final CoreException e) {

        }

        return equinoxProperties;
    }

    @Override
    public List<String> getVMArguments(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration) throws CoreException
    {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return Collections.emptyList();
        }

        final List<String> arguments = new ArrayList<String>();

        arguments.add("-Dservicemix.home=" + platformModel.getParentKarafModel().getRootDirectory());
        arguments.add("-Dservicemix.base=" + platformModel.getParentKarafModel().getRootDirectory());

        arguments.add("-Dservicemix.startLocalConsole=true");
        arguments.add("-Dservicemix.startRemoteShell=true");

        arguments.add("-Djava.util.logging.config.file=" + platformModel.getParentKarafModel().getConfigurationDirectory() + "/java.util.logging.properties");

        return arguments;
    }

    @Override
    public void initialize(final KarafWorkingPlatformModel platformModel,
            final ILaunchConfigurationWorkingCopy configuration) {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return;
        }
    }

    @Override
    public void launch(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration,
            final String mode,
            final ILaunch launch,
            final IProgressMonitor monitor) throws CoreException
    {
        if (!(platformModel.getParentKarafModel() instanceof ServiceMixKernelPlatformModel)) {
            return;
        }
    }

}
