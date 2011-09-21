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
package org.apache.karaf.eclipse.ui.internal;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafPlatformModelRegistry;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafLaunchConfigurationConstants;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.ui.project.KarafProject;

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
