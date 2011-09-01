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
