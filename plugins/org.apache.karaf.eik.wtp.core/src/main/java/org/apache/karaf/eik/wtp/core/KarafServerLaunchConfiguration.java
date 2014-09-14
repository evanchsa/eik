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
package org.apache.karaf.eik.wtp.core;

import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.core.model.WorkingKarafPlatformModel;
import org.apache.karaf.eik.ui.KarafLaunchConfigurationDelegate;
import org.apache.karaf.eik.wtp.core.server.KarafServerBehavior;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;

public class KarafServerLaunchConfiguration extends KarafLaunchConfigurationDelegate {

    private IServer server;

    private KarafServerBehavior karafServer;

    @Override
    public IVMRunner getVMRunner(final ILaunchConfiguration configuration, final String mode) throws CoreException {
        if(ILaunchManager.PROFILE_MODE.equals(mode)) {
            // TODO: Figure out how to setup profiling
            return super.getVMRunner(configuration, ILaunchManager.RUN_MODE);
        }

        return super.getVMRunner(configuration, mode);
    }

    @Override
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
        super.launch(configuration, mode, launch, monitor);

        if (server.shouldPublish() && ServerCore.isAutoPublishing()) {

            server.publish(IServer.PUBLISH_INCREMENTAL, monitor);

            monitor.worked(10);
        }

        karafServer.configureLaunch(launch, mode, monitor);
    }

    @Override
    protected void preLaunchCheck(
            final ILaunchConfiguration configuration,
            final ILaunch launch,
            final IProgressMonitor monitor)
        throws CoreException
    {
        super.preLaunchCheck(configuration, launch, monitor);

        server = ServerUtil.getServer(configuration);

        if (server == null) {
            return;
        }

        monitor.worked(5);

        final IPath runtimeLocation = server.getRuntime().getLocation();
        this.karafPlatform = KarafPlatformModelRegistry.findPlatformModel(runtimeLocation);

        final IPath workingArea = new Path(getConfigDir(configuration).getAbsolutePath());
        workingKarafPlatform = new WorkingKarafPlatformModel(workingArea, karafPlatform);

        karafServer = (KarafServerBehavior) server.loadAdapter(KarafServerBehavior.class, null);

        monitor.worked(10);
    }

}
