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
package org.apache.karaf.eclipse.ui.project.impl;

import java.util.List;
import java.util.Map;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.ui.internal.KarafLaunchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.internal.core.target.provisional.IBundleContainer;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;
import org.eclipse.pde.internal.core.target.provisional.ITargetHandle;
import org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class TargetDefinitionBuildUnit extends AbstractKarafBuildUnit {


    /**
     *
     * @param karafPlatformModel
     * @param karafProject
     * @param projectBuilder
     */
    public TargetDefinitionBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject, final IncrementalProjectBuilder projectBuilder) {
        super(karafPlatformModel, karafProject, projectBuilder);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        createTargetPlatform(monitor);
    }

    /**
     * @return
     */
    private IFile createTargetDefinitionFile() {
        final String projectName = getKarafProject().getName();
        final IPath targetFilename = new Path(projectName).addFileExtension("target");
        return getKarafProject().getProjectHandle().getFile(targetFilename);
    }

    /**
     *
     * @param monitor
     * @throws CoreException
     */
    private void createTargetPlatform(final IProgressMonitor monitor) throws CoreException {
        final ITargetPlatformService targetPlatformService =
            (ITargetPlatformService) KarafUIPluginActivator.getDefault().getService(ITargetPlatformService.class.getName());

        final IFile targetLocation = createTargetDefinitionFile();
        final ITargetHandle targetHandle = targetPlatformService.getTarget(targetLocation);

        final ITargetDefinition target = targetHandle.getTargetDefinition();

        target.setName(getKarafProject().getName());

        final List<IBundleContainer> bundleContainers = KarafLaunchUtils.getBundleContainers(getKarafPlatformModel());
        target.setBundleContainers(bundleContainers.toArray(new IBundleContainer[0]));

        targetPlatformService.saveTargetDefinition(target);
    }
}
