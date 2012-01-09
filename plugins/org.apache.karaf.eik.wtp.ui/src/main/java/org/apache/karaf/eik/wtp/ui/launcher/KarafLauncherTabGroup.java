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
package org.apache.karaf.eik.wtp.ui.launcher;

import org.apache.karaf.eik.ui.KarafConfigurationTab;
import org.apache.karaf.eik.ui.KarafLaunchConfigurationInitializer;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.ui.launcher.OSGiSettingsTab;
import org.eclipse.pde.ui.launcher.TracingTab;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;

public class KarafLauncherTabGroup extends AbstractLaunchConfigurationTabGroup {

    /**
     * Creates the necessary tabs for this launch configuration.
     *
     * @see ILaunchConfigurationTabGroup
     */
    @Override
    public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
        final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new ServerLaunchConfigurationTab(new String[] { "org.apache.karaf.eik.server" }),
                new JavaArgumentsTab(),
                new OSGiSettingsTab(),
                new TracingTab(),
                new KarafConfigurationTab(),
                new EnvironmentTab(),
                new CommonTab()
        };
        setTabs(tabs);
    }

    /**
     * Sets the defaults for the tab group and launch configuration using
     * {@link KarafLaunchConfigurationInitializer} in <b>addition</b> to the
     * defaults set by the OSGi framework.
     *
     * @param configuration
     *            the working copy of the launch configuration
     */
    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
    }

}
