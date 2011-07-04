/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui.internal;

import info.evanchik.eclipse.karaf.core.shell.KarafRemoteShellConnection;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A s{@link Job} that will connect to a Karaf SSH server using the specified
 * {@link KarafRemoteShellConnection}
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafRemoteShellConnectJob extends Job {

    private final KarafRemoteShellConnection karafRemoteShellConnection;

    /**
     * Creates a {@link Job} that will connect to a Karaf SSH server using the
     * specified {@link KarafRemoteShellConnection}
     *
     * @param name
     *            the name of the {@code Job}
     * @param karafRemoteShellConnection
     *            the {@code KarafRemoteShellConnection} to use
     */
    public KarafRemoteShellConnectJob(final String name, final KarafRemoteShellConnection karafRemoteShellConnection) {
        super(name);

        this.karafRemoteShellConnection = karafRemoteShellConnection;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {

        try {
            monitor.worked(1);

            karafRemoteShellConnection.connect();
            if (monitor.isCanceled()) {
                karafRemoteShellConnection.disconnect();

                return Status.CANCEL_STATUS;
            }

            monitor.done();
        } catch (final Exception e) {
            return new Status(Status.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to connect to Karaf remote shell", e);
        }

        return Status.OK_STATUS;
    }

    public boolean isConnected() {
        return karafRemoteShellConnection.isConnected();
    }
}
