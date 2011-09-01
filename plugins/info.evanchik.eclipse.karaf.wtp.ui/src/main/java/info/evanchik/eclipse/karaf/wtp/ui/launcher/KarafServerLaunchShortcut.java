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
package info.evanchik.eclipse.karaf.wtp.ui.launcher;

import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationInitializer;

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
