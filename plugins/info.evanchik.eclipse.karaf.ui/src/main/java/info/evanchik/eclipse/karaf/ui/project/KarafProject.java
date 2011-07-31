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

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

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

    private final IProject project;

    /**
     *
     * @param project
     * @return
     */
    public static boolean isKarafProject(final IProject project) {
        try {
            final String karafProject = project.getPersistentProperty(
                    new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafProject"));

            return karafProject != null;
        } catch (final CoreException e) {

        }

        return false;
    }

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

                KarafUIPluginActivator.getLogger().error("Unable to find Karaf Platform", e);
                return null;
            }
        } else {
            return super.getAdapter(adapter);
        }
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

        }

        return null;
    }

    @Override
    public IProject getProjectHandle() {
        return project;
    }
}
