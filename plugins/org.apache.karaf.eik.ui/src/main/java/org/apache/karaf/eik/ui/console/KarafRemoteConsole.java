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
package org.apache.karaf.eik.ui.console;

import org.apache.karaf.eik.core.shell.KarafRemoteShellConnection;
import org.apache.karaf.eik.core.shell.KarafSshConnectionUrl;
import org.apache.karaf.eik.core.shell.KarafSshShellConnection;
import org.apache.karaf.eik.ui.KarafLaunchConfigurationConstants;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.internal.KarafRemoteShellConnectJob;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;

public class KarafRemoteConsole extends IOConsole implements IDebugEventSetListener {

    public static final String KARAF_REMOTE_CONSOLE_TYPE = "org.apache.karaf.eik.console.remote";

    private final IConsoleColorProvider colorProvider;

    private final IOConsoleInputStream inputStream;

    private final IProcess process;

    private KarafRemoteShellConnection shellConnection;

    public KarafRemoteConsole(
            final IProcess process,
            final KarafSshConnectionUrl connectionUrl,
            final KarafSshShellConnection.Credentials credentials,
            final IConsoleColorProvider colorProvider,
            final String name,
            final String encoding) {
        super(
            name,
            KARAF_REMOTE_CONSOLE_TYPE,
            KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor(KarafUIPluginActivator.LOGO_16X16_IMG),
            encoding,
            true);

        this.process = process;
        this.inputStream = getInputStream();
        this.colorProvider = colorProvider;

        final Color color = this.colorProvider.getColor(IDebugUIConstants.ID_STANDARD_INPUT_STREAM);
        this.inputStream.setColor(color);

        final InputStream noAvailableInputStream = new FilterInputStream(inputStream) {
            @Override
            public int available() throws IOException {
                return 0;
            }
        };

        setName(computeName());
        final IOConsoleOutputStream outputStream = newOutputStream();

        boolean remoteShellEnabled = false;
        try {
            final ILaunchConfiguration configuration = process.getLaunch().getLaunchConfiguration();
            remoteShellEnabled =
                configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                    false);
        } catch (final CoreException e) {
            return;
        }

        if (remoteShellEnabled) {
            shellConnection =
                new KarafSshShellConnection(
                    connectionUrl,
                    credentials,
                    noAvailableInputStream,
                    outputStream,
                    outputStream);

            final KarafRemoteShellConnectJob job = new KarafRemoteShellConnectJob(name, shellConnection);
            job.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(final IJobChangeEvent event) {
                    if (!event.getResult().isOK()) {
                        final Throwable t = event.getResult().getException();
                        writeTo(outputStream, "Unable to connect to SSH server: " + (t != null ? t.getLocalizedMessage() : "Unknown error"));
                    }
                }
            });

            DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {

                @Override
                public void handleDebugEvents(final DebugEvent[] events) {

                    for (final DebugEvent event : events) {
                        if (   process != null
                            && process.equals(event.getSource())
                            && event.getKind() == DebugEvent.TERMINATE)
                        {
                            job.cancel();
                        }
                    }
                }
            });

            job.schedule(15 * 1000);
        } else {
            writeTo(outputStream, "The Karaf remote shell is disabled. Enable it in the launch configuration dialog.");
        }
    }

    private void writeTo(final IOConsoleOutputStream outputStream, final String message) {
        try {
            outputStream.write(message);
            outputStream.flush();
        } catch (final IOException e) {
            // Do nothing
        }
    }

    /**
     * Returns the {@link IProcess} for this console
     *
     * @return the {@code IProcess} for this console
     */
    public IProcess getProcess() {
        return process;
    }

    @Override
    public void handleDebugEvents(final DebugEvent[] events) {
        for (final DebugEvent event : events) {
            if (event.getSource().equals(process)) {
                if (event.getKind() == DebugEvent.TERMINATE) {
                    if (shellConnection != null) {
                        try {
                            shellConnection.disconnect();
                        } catch (final IOException e) {
                            KarafUIPluginActivator.getLogger().error("Unable to disconnect from SSH server", e);
                        }
                    }
                    DebugPlugin.getDefault().removeDebugEventListener(this);

                    resetName();
                }
            }
        }
    }

    /**
     * Computes and returns the current name of this console.
     *
     * @return a name for this console
     */
    protected String computeName() {
        String label = null;
        final IProcess process = getProcess();
        final ILaunchConfiguration config = process.getLaunch().getLaunchConfiguration();

        label = process.getAttribute(IProcess.ATTR_PROCESS_LABEL);
        if (label == null) {
            if (config == null) {
                label = process.getLabel();
            } else {
                // check if PRIVATE config
                if (DebugUITools.isPrivate(config)) {
                    label = process.getLabel();
                } else {
                    String type = null;
                    try {
                        type = config.getType().getName();
                    } catch (final CoreException e) {
                    }
                    final StringBuffer buffer = new StringBuffer();
                    buffer.append("Remote shell connection to: ");
                    buffer.append(config.getName());
                    if (type != null) {
                        buffer.append(" ["); //$NON-NLS-1$
                        buffer.append(type);
                        buffer.append("] "); //$NON-NLS-1$
                    }
                    buffer.append(process.getLabel());
                    label = buffer.toString();
                }
            }
        }

        if (process.isTerminated()) {
            return MessageFormat.format("<disconnected> {0}", (Object[]) new String[] { label });
        }
        return label;
    }

    @Override
    protected void dispose() {
        super.dispose();

        if (DebugPlugin.getDefault() != null) {
            DebugPlugin.getDefault().removeDebugEventListener(this);
        }
    }

    @Override
    protected void init() {
        super.init();

        if (process.isTerminated()) {
            if (shellConnection != null) {
                try {
                    shellConnection.disconnect();
                } catch (final IOException e) {
                    KarafUIPluginActivator.getLogger().error("Unable to disconnect from SSH server", e);
                }
            }
        } else {
            DebugPlugin.getDefault().addDebugEventListener(this);
        }
    }

    private void resetName() {
        final String newName = computeName();
        final String name = getName();
        if (!name.equals(newName)) {
            final UIJob job = new UIJob("Update console title") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(final IProgressMonitor monitor) {
                     KarafRemoteConsole.this.setName(newName);
                     return Status.OK_STATUS;
                }
            };
            job.setSystem(true);
            job.schedule();
        }
    }

}
