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
package org.apache.karaf.eclipse.ui.project;

import java.util.Properties;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafPlatformModelRegistry;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.QualifiedName;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafProject extends PlatformObject implements IKarafProject {

    /**
     * Determines if the {@link IProject} is actually an {@link IKarafProject}
     *
     * @param project
     *            the {@code IProject} to test
     * @return true if the specified {@code IProject} is a {@code IKarafProject}
     *         ; false otherwise
     */
    public static boolean isKarafProject(final IProject project) {
        try {
            final String karafProject = project.getPersistentProperty(
                    new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafProject"));

            return karafProject != null;
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().warn("Unable to determine if " + project.getName() + " is a Karaf Project", e);
        }

        return false;
    }

    private final IProject project;

    /**
     *
     * @param project
     */
    public KarafProject(final IProject project) {
        this.project = project;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
        if (KarafPlatformModel.class.equals(adapter)) {
            try {
                return KarafPlatformModelRegistry.findPlatformModel(getPlatformRootDirectory());
            } catch (final CoreException e) {
                KarafUIPluginActivator.getLogger().error("Unable to find Karaf Platform at the root directory: " + getPlatformRootDirectory().toOSString(), e);
                return null;
            }
        } else if(IProject.class.equals(adapter)) {
            return project;
        } else {
            return super.getAdapter(adapter);
        }
    }

    @Override
    public IFile getFile(final String name) {
        return project.getFile(IKarafProject.ROOT_PATH.append(name));
    }

    @Override
    public IFolder getFolder(final String name) {
        return project.getFolder(IKarafProject.ROOT_PATH.append(name));
    }

    @Override
    public IPath getLocation() {
        return project.getLocation();
    }

    @Override
    public String getName() {
        return project.getName();
    }

    @Override
    public IFile getPlatformFile(final String name) {
        return getFile(IKarafProject.ROOT_PLATFORM_PATH.append(name).toOSString());
    }

    @Override
    public IPath getPlatformRootDirectory() {
        try {
            final String karafModelPath = project.getPersistentProperty(
                    new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafModel"));

            return new Path(karafModelPath);
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to determine platform root directory for project: " + project.getName(), e);
        }

        return null;
    }

    @Override
    public IProject getProjectHandle() {
        return project;
    }

    @Override
    public Properties getRuntimeProperties() {
        try {
            return KarafCorePluginUtils.loadProperties(
                    project.getFolder("runtime").getRawLocation().toFile(),
                    "runtime.properties");
        } catch (final CoreException e) {
            return new Properties();
        }
    }
}
