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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;

public class KarafRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {

    private static final int MAX_SEARCH_DEPTH = 50;

    @Override
    public IClasspathEntry[] resolveClasspathContainer(final IProject project, final IRuntime runtime) {
        final IPath installPath = runtime.getLocation();

        if (installPath == null) {
            return new IClasspathEntry[0];
        }

        try {
            final KarafPlatformModel karafPlatform = KarafPlatformModelRegistry.findPlatformModel(installPath);

            final File pluginRootDirectory = karafPlatform.getPluginRootDirectory().toFile();
            final List<File> jarFiles = new ArrayList<File>();

            KarafCorePluginUtils.getJarFileList(pluginRootDirectory, jarFiles, MAX_SEARCH_DEPTH);

            final List<IClasspathEntry> list = resolveLibraryEntries(jarFiles);

            return list.toArray(new IClasspathEntry[0]);
        } catch (final CoreException e) {
        }

        return new IClasspathEntry[0];
    }

    /**
     * Converts the list of JAR files to {@link IClasspathEntry}s
     *
     * @param files
     *            the {@link List} of files that will be converted
     * @return a {@link List} of {@code IClasspathEntry}s for each file in the
     *         original list of files
     */
    private static List<IClasspathEntry> resolveLibraryEntries(final List<File> files) {
        final List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();

        for (final File f : files) {
            final IPath path = new Path(f.getAbsolutePath());
            classpathEntries.add(JavaCore.newLibraryEntry(path, null, null));
        }

        return classpathEntries;
    }

}
