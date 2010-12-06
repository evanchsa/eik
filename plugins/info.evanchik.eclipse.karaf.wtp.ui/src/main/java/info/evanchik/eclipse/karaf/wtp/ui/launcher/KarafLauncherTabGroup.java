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
package info.evanchik.eclipse.karaf.wtp.ui.launcher;

import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationInitializer;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafLauncherTabGroup extends AbstractLaunchConfigurationTabGroup {

    /**
     * Creates the necessary tabs for this launch configuration.
     *
     * @see ILaunchConfigurationTabGroup
     */
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new ServerLaunchConfigurationTab(new String[] { "info.evanchik.eclipse.server.karaf" }), new JavaArgumentsTab(),
                new OSGiSettingsTab(), new TracingTab(), new EnvironmentTab(), new CommonTab() }; //$NON-NLS-1$
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
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
    }
}
