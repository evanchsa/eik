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
package org.apache.karaf.eclipse.ui.workbench;

import org.apache.karaf.eclipse.core.KarafWorkingPlatformModel;
import org.apache.karaf.eclipse.core.equinox.BundleEntry;

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
