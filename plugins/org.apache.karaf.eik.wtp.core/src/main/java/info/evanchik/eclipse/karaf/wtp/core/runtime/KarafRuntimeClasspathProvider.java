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
package info.evanchik.eclipse.karaf.wtp.core.runtime;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
