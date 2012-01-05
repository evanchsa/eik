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
package info.evanchik.eclipse.karaf.ui.workbench;

import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafWorkbenchService {

    /**
     *
     * @param platformModel
     * @param configuration
     * @return
     */
    public List<BundleEntry> getAdditionalBundles(KarafWorkingPlatformModel platformModel, ILaunchConfiguration configuration);

    /**
     * Gets the {@link Map<String, String>} of additional Equinox configuration
     * properties
     *
     * @param platformModel
     * @param configuration
     * @return the {@code Map<String, String>} of additional Equinox
     *         configuration
     */
    public Map<String, String> getAdditionalEquinoxConfiguration(KarafWorkingPlatformModel platformModel, ILaunchConfiguration configuration);

    /**
     *
     * @param configuration
     * @return
     * @throws CoreException
     */
    public List<String> getVMArguments(KarafWorkingPlatformModel platformModel, ILaunchConfiguration configuration) throws CoreException;

    /**
     * Called when the {@link ILaunchConfiguration} is being initialized
     *
     * @param platformModel
     *            the {@link KarafWorkingPlatformModel} that this launch
     *            configuration is executing against
     * @param configuration
     *            the {@code ILaunchConfigurationWorkingCopy}
     */
    public void initialize(KarafWorkingPlatformModel platformModel, ILaunchConfigurationWorkingCopy configuration);

    /**
     *
     * @param configuration
     * @param mode
     * @param launch
     * @param monitor
     * @throws CoreException
     */
    public void launch(KarafWorkingPlatformModel platformModel, ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException;
}
