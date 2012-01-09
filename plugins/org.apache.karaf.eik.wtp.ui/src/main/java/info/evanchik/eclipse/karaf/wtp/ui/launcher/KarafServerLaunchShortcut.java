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

import org.apache.karaf.eik.ui.KarafLaunchConfigurationInitializer;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafServerLaunchShortcut extends OSGiLaunchShortcut {

    @Override
    protected String getLaunchConfigurationTypeName() {
        return "info.evanchik.eclipse.karaf.wtp.ui.KarafLauncher"; //$NON-NLS-1$
    }

    @Override
    protected void initializeConfiguration(
            ILaunchConfigurationWorkingCopy configuration) {
        super.initializeConfiguration(configuration);

        KarafLaunchConfigurationInitializer.initializeConfiguration(configuration);
    }

}
