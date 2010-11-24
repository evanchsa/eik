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

import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationDelegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

    /**
     * The WST Server object for this launch configuration. This is only
     * non-null when the launch configuration has been created through the WTP
     * functionality.
     */
    private IServer server;

    /**
     * The Karaf server as defined by the necessary WST extensions. This object
     * encapsulates the WST-specific behavior and will be non-null only when
     * this launch configuration has been created via the WTP functionality.
     */
    private KarafServerBehavior karafServer;

    @Override
    public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
        if(ILaunchManager.PROFILE_MODE.equals(mode)) {
            // TODO: Figure out how to setup profiling
            return super.getVMRunner(configuration, ILaunchManager.RUN_MODE);
        }

        return super.getVMRunner(configuration, mode);
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        super.launch(configuration, mode, launch, monitor);

        if (server.shouldPublish() && ServerCore.isAutoPublishing()) {

            server.publish(IServer.PUBLISH_INCREMENTAL, monitor);

            monitor.worked(10);
        }

        karafServer.configureLaunch(launch, mode, monitor);
    }

    protected void loadKarafPlatform(ILaunchConfiguration configuration, ILaunch launch,
                    IProgressMonitor monitor) throws CoreException {
        server = ServerUtil.getServer(configuration);

        if (server == null) {
            return;
        }

        monitor.worked(5);

        this.karafPlatform = new GenericKarafPlatformModel(server.getRuntime().getLocation());

        karafServer = (KarafServerBehavior) server.loadAdapter(KarafServerBehavior.class, null);

        monitor.worked(10);
    }

}
