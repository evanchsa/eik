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

import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.core.configuration.StartupSection;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationInitializer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafServerLaunchConfigurationInitializer extends KarafLaunchConfigurationInitializer {

    private IServer server;

    @Override
    protected void loadKarafPlatform(final ILaunchConfigurationWorkingCopy configuration) {
        try {
            server = ServerUtil.getServer(configuration);

            if (server == null) {
                return;
            }

            this.karafPlatform = KarafPlatformModelRegistry.findPlatformModel(server.getRuntime().getLocation());

            this.startupSection = (StartupSection)Platform.getAdapterManager().getAdapter(this.karafPlatform, StartupSection.class);
            this.startupSection.load();
        } catch (final CoreException e) {
        }
    }
}
