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
package info.evanchik.eclipse.karaf.ui.console;

import info.evanchik.eclipse.karaf.core.shell.KarafRemoteShellConnection;
import info.evanchik.eclipse.karaf.core.shell.KarafSshShellConnection;
import info.evanchik.eclipse.karaf.core.shell.KarafSshShellConnection.KarafSshConnectionUrl;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationConstants;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.internal.KarafRemoteShellConnectJob;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafRemoteConsole extends IOConsole implements IDebugEventSetListener {

    public static final String KARAF_REMOTE_CONSOLE_TYPE = "info.evanchik.eclipse.karaf.console.remote";

    private final IConsoleColorProvider colorProvider;

    private final IOConsoleInputStream inputStream;

    private final IProcess process;

    private KarafRemoteShellConnection shellConnection;

    /**
     *
     * @param process
     * @param connectionUrl
     * @param colorProvider
     * @param name
     * @param encoding
     */
    public KarafRemoteConsole(
            final IProcess process,
            final KarafSshConnectionUrl connectionUrl,
            final IConsoleColorProvider colorProvider,
            final String name,
            final String encoding)
    {
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
            remoteShellEnabled = configuration.getAttribute(
                    KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE,
                    false);
        } catch (final CoreException e) {
            return;
        }

        if (remoteShellEnabled) {
            shellConnection = new KarafSshShellConnection(connectionUrl, noAvailableInputStream, outputStream, outputStream);

            final KarafRemoteShellConnectJob job = new KarafRemoteShellConnectJob(name, shellConnection);
            job.schedule(15 * 1000);
        } else {
            try {
                outputStream.write("The Karaf remote shell is disabled. Enable it in the launch configuration dialog");
                outputStream.flush();
            } catch (final IOException e) {
                // Do nothing
            }
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
                        shellConnection.disconnect();
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

        DebugPlugin.getDefault().removeDebugEventListener(this);
    }

    @Override
    protected void init() {
        super.init();

        if (process.isTerminated()) {
            if (shellConnection != null) {
                shellConnection.disconnect();
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
