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
package info.evanchik.eclipse.felix;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FelixInitializer extends OSGiLaunchConfigurationInitializer {

    @Override
    protected String getAutoStart(String bundleID) {
        return super.getAutoStart(bundleID);
    }

    @Override
    protected String getStartLevel(String bundleID) {
        return super.getStartLevel(bundleID);
    }

    @Override
    protected void initializeBundleState(ILaunchConfigurationWorkingCopy configuration) {
        super.initializeBundleState(configuration);
    }

    @Override
    protected void initializeFrameworkDefaults(ILaunchConfigurationWorkingCopy configuration) {
        super.initializeFrameworkDefaults(configuration);
    }

}
