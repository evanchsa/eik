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
package org.apache.karaf.eik.ui.project.impl;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.ui.IKarafProject;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.VariablesPlugin;

public class KarafPlatformResourcesBuildUnit extends AbstractKarafBuildUnit {

    public KarafPlatformResourcesBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final IKarafProject newKarafProject = getKarafProject();
        final IProject project = newKarafProject.getProjectHandle();

        for (final String folderName : new String[] { ".bin", ".bin/platform", ".bin/runtime" }) {
            final IFolder folder = project.getFolder(folderName);
            if (!folder.exists()) {
                folder.create(true, true, monitor);
            }
        }

        if (!project.getFolder(".bin/platform/etc").exists()) {
            project.getFolder(".bin/platform/etc").createLink(getKarafPlatformModel().getConfigurationDirectory(), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/deploy").exists()) {
            project.getFolder(".bin/platform/deploy").createLink(getKarafPlatformModel().getUserDeployedDirectory(), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/lib").exists()) {
            project.getFolder(".bin/platform/lib").createLink(getKarafPlatformModel().getRootDirectory().append("lib"), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/system").exists()) {
            project.getFolder(".bin/platform/system").createLink(getKarafPlatformModel().getPluginRootDirectory(), 0, monitor);
        }

        // TODO: Is this the right way to add the current installation?
        final IDynamicVariable eclipseHomeVariable = VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable("eclipse_home");
        final String eclipseHome = eclipseHomeVariable.getValue("");

        if (!project.getFolder(".bin/platform/eclipse").exists()) {
            project.getFolder(".bin/platform/eclipse").create(true, true, monitor);
        }

        if (!project.getFolder(".bin/platform/eclipse/dropins").exists()) {
            project.getFolder(".bin/platform/eclipse/dropins").createLink(new Path(eclipseHome).append("dropins"), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/eclipse/plugins").exists()) {
            project.getFolder(".bin/platform/eclipse/plugins").createLink(new Path(eclipseHome).append("plugins"), 0, monitor);
        }
    }

}
