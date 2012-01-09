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
package info.evanchik.eclipse.karaf.ui.internal;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationConstants;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.project.KarafProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafLaunchConfigurationAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {

        // TODO: This entire method is ugly and needs to be cleaned up

        final Object adapted;
        if (     KarafPlatformModel.class.equals(adapterType)
              && adaptableObject instanceof ILaunchConfiguration)
        {
            final ILaunchConfiguration configuration = (ILaunchConfiguration) adaptableObject;
            try {
                if (configuration.getAttributes().containsKey(KarafLaunchConfigurationConstants.KARAF_LAUNCH_SOURCE_RUNTIME)) {
                    final String platformPath = (String) configuration.getAttributes().get(KarafLaunchConfigurationConstants.KARAF_LAUNCH_SOURCE_RUNTIME);
                    adapted = KarafPlatformModelRegistry.findPlatformModel(new Path(platformPath));
                } else {
                    adapted = null;
                }
            } catch (final CoreException e) {
                KarafUIPluginActivator.getLogger().error("Unable to find Karaf Platform model", e);
                return null;
            }
        } else if (    IKarafProject.class.equals(adapterType)
                    && adaptableObject instanceof KarafPlatformModel)
        {
            IKarafProject karafProject = null;
            final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) adaptableObject;
            for (final IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                if (KarafProject.isKarafProject(project)) {
                    karafProject = new KarafProject(project);
                    if (karafPlatformModel.getRootDirectory().equals(karafProject.getPlatformRootDirectory())) {
                        break;
                    }
                }
            }

            adapted = karafProject;
        } else if (    IKarafProject.class.equals(adapterType)
                    && adaptableObject instanceof IProject)
        {
            if (KarafProject.isKarafProject((IProject) adaptableObject)) {
                adapted = new KarafProject((IProject) adaptableObject);
            } else {
                return null;
            }
        } else {
            adapted = null;
        }

        return adapted;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { KarafPlatformModel.class, IKarafProject.class };
    }
}
