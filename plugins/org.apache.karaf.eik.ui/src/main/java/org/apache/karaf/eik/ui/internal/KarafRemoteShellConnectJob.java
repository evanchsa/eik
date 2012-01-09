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
package org.apache.karaf.eik.ui.internal;

import org.apache.karaf.eik.core.shell.KarafRemoteShellConnection;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A s{@link Job} that will connect to a Karaf SSH server using the specified
 * {@link KarafRemoteShellConnection}
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

        monitor.beginTask("Connecting to Karaf remote shell: " + getName(), 1);

        try {

            karafRemoteShellConnection.connect();

            monitor.worked(1);

            if (monitor.isCanceled()) {
                karafRemoteShellConnection.disconnect();

                return Status.CANCEL_STATUS;
            }
        } catch (final Exception e) {
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            } else {
                return new Status(Status.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to connect to Karaf remote shell", e);
            }
        } finally {
            monitor.done();
        }

        return Status.OK_STATUS;
    }

    public boolean isConnected() {
        return karafRemoteShellConnection.isConnected();
    }

}
