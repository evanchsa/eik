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

import org.apache.karaf.eik.ui.KarafLaunchConfigurationInitializer;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;

public class KarafServerLaunchShortcut extends OSGiLaunchShortcut {

    @Override
    protected String getLaunchConfigurationTypeName() {
        return "org.apache.karaf.eik.wtp.ui.KarafLauncher";
    }

    @Override
    protected void initializeConfiguration(
            ILaunchConfigurationWorkingCopy configuration) {
        super.initializeConfiguration(configuration);

        KarafLaunchConfigurationInitializer.initializeConfiguration(configuration);
    }

}
