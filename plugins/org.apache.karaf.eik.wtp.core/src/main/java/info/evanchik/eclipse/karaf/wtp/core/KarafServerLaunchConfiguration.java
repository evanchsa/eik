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
package info.evanchik.eclipse.karaf.wtp.core;

import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationDelegate;
import info.evanchik.eclipse.karaf.wtp.core.server.KarafServerBehavior;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
