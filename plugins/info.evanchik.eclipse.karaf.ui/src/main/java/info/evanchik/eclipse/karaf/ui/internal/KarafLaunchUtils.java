/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package info.evanchik.eclipse.karaf.ui.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.pde.internal.core.target.DirectoryBundleContainer;
import org.eclipse.pde.internal.core.target.provisional.IBundleContainer;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafLaunchUtils {

    private static final int MAX_DIRECTORY_RECURSE_DEPTH = 50;

    @SuppressWarnings("restriction")
    public static List<IBundleContainer> getBundleContainers(final KarafPlatformModel karafPlatformModel) {
        final Collection<File> directories = KarafLaunchUtils.getJarDirectories(karafPlatformModel);

        final List<IBundleContainer> bundleContainers = new ArrayList<IBundleContainer>();
        for (final File dir : directories) {
            bundleContainers.add(new DirectoryBundleContainer(dir.getAbsolutePath()));
        }

        return bundleContainers;
    }

    /**
     * Constructs an array of directories that contain plugins in the Karaf
     * platform. This array includes the {@code lib} directory as well as all
     * directories that contain JARs under {@code system} and {@code deploy}.
     *
     * @param karafPlatformModel
     *            the Karaf Platform
     * @return the directories that contain JARs in the Karaf platform
     */
    public static Collection<File> getJarDirectories(final KarafPlatformModel karafPlatformModel) {
        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(
                karafPlatformModel.getPluginRootDirectory().toFile(),
                jarFiles,
                MAX_DIRECTORY_RECURSE_DEPTH);

        KarafCorePluginUtils.getJarFileList(
                karafPlatformModel.getUserDeployedDirectory().toFile(),
                jarFiles,
                MAX_DIRECTORY_RECURSE_DEPTH);

        // Add each JAR file's directory to the list of directories that contain
        // plugins
        final Set<File> directories = new HashSet<File>();
        for (final File f : jarFiles) {
            directories.add(f.getParentFile());
        }

        // Add the lib directory for completeness, if the developer is pulling a
        // target platform they better know what the are doing
        directories.add(karafPlatformModel.getRootDirectory().append("lib").toFile()); // $NON-NLS-1$

        return directories;
    }

    private KarafLaunchUtils() {
        throw new AssertionError("Do not instantiate");
    }
}
