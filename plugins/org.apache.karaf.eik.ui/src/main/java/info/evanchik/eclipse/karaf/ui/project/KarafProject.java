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
package info.evanchik.eclipse.karaf.ui.project;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.util.Properties;

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

    private static final IPath ROOT_PATH = new Path(".bin");

    public static final IPath ROOT_PLATFORM_PATH = ROOT_PATH.append("platform");

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
        return project.getFile(ROOT_PATH.append(name));
    }

    @Override
    public IFolder getFolder(final String name) {
        return project.getFolder(ROOT_PATH.append(name));
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
