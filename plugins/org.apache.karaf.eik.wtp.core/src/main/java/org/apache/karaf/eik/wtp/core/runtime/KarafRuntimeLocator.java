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
package org.apache.karaf.eik.wtp.core.runtime;

import org.apache.karaf.eik.wtp.core.KarafWtpPluginActivator;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;

public class KarafRuntimeLocator extends RuntimeLocatorDelegate {

    /**
     * Maximum depth to search for a Karaf server runtime
     */
    public static final int MAX_DEPTH = 4;

    @Override
    public void searchForRuntimes(final IPath path, final IRuntimeSearchListener listener,
            final IProgressMonitor monitor) {

        final File[] files;
        if (path == null) {
            files = File.listRoots();
        } else if (path.toFile().exists()) {
            files = path.toFile().listFiles();
        } else {
            monitor.worked(100);
            return;
        }

        final int workUnit = 100 / files.length;

        for (final File f : files) {
            if (monitor.isCanceled()) {
                return;
            }

            if (f != null && f.isDirectory()) {
                searchDirectory(f, MAX_DEPTH, listener, monitor);
                monitor.worked(workUnit);
            }
        }

        monitor.worked(100 - workUnit * files.length);
    }

    /**
     * Searches the given directory and all directories recursively to the given
     * depth for Karaf server runtimes.
     *
     * @param directory
     *            the current directory that is being searched
     * @param depth
     *            the max depth to search
     * @param listener
     *            the listener that will be notified if a Karaf server runtime
     *            is found
     * @param monitor
     *            the progress monitor
     */
    private void searchDirectory(final File directory, final int depth,
            final IRuntimeSearchListener listener, final IProgressMonitor monitor) {

        final IRuntimeWorkingCopy runtime = resolveDirectoryToRuntime(directory, monitor);

        if (runtime != null) {
            listener.runtimeFound(runtime);
        }

        if (depth == 0 || monitor.isCanceled()) {
            return;
        }

        final File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory();
            }
        });

        if (files == null) {
            return;
        }

        for (final File f : files) {
            if (monitor.isCanceled()) {
                return;
            }

            searchDirectory(f, depth - 1, listener, monitor);
        }

    }

    /**
     * Attempts to resolve the directory to a WTP server runtime according to
     * the registered runtime type identifiers.
     *
     * @param directory
     *            the directory that is being examined
     * @param monitor
     *            the progress monitor
     * @return a valid {@link IRuntimeWorkingCopy} if a runtime has been found,
     *         or null if it has not been found
     */
    private IRuntimeWorkingCopy resolveDirectoryToRuntime(final File directory,
            final IProgressMonitor monitor) {
        for (final String runtimeId : KarafWtpPluginActivator.RUNTIME_TYPE_IDS) {
            try {
                final IRuntimeType runtimeType = ServerCore.findRuntimeType(runtimeId);

                final String absolutePath = directory.getAbsolutePath();
                final String id = absolutePath.replace(File.separatorChar, '_').replace(':', '-');

                final IRuntimeWorkingCopy runtime = runtimeType.createRuntime(id, monitor);
                runtime.setName(directory.getName());
                runtime.setLocation(new Path(absolutePath));

                final IStatus status = runtime.validate(monitor);
                if (status == null || status.getSeverity() != IStatus.ERROR) {
                    return runtime;
                }

                // TODO: Log something?
            } catch (final Exception e) {
                // TODO : Logging
            }
        }

        return null;
    }

}
