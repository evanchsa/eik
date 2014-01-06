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
package org.apache.karaf.eik.ui.project;

import java.lang.reflect.InvocationTargetException;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafLaunchConfigurationInitializer;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class NewKarafProjectOperation extends WorkspaceModifyOperation {

    private final KarafPlatformModel karafPlatformModel;

    private final IKarafProject newKarafProject;

    private final KarafWorkingPlatformModel workingPlatformModel;

    public NewKarafProjectOperation(
            final KarafPlatformModel karafPlatformModel,
            final KarafWorkingPlatformModel workingPlatformModel,
            final IKarafProject newKarafProject)
    {
        this.karafPlatformModel = karafPlatformModel;
        this.newKarafProject = newKarafProject;
        this.workingPlatformModel = workingPlatformModel;
    }

    @Override
    protected void execute(final IProgressMonitor monitor)
        throws CoreException, InvocationTargetException, InterruptedException
    {
        monitor.beginTask("Creating Apache Karaf project", 3);

        createProject(monitor);

        monitor.worked(1);

        addNatureToProject(KarafProjectNature.ID, monitor);

        monitor.worked(1);

        createKarafPlatformResources(monitor);

        monitor.worked(1);

        newKarafProject.getProjectHandle().refreshLocal(2, monitor);

        monitor.done();
    }

    private void addNatureToProject(final String natureId, final IProgressMonitor monitor) throws CoreException {
        final IProject project = newKarafProject.getProjectHandle();

        final IProjectDescription description = project.getDescription();
        final String[] prevNatures = description.getNatureIds();
        final String[] newNatures = new String[prevNatures.length + 1];

        System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

        newNatures[prevNatures.length] = natureId;
        description.setNatureIds(newNatures);
        project.setDescription(description, monitor);
    }

    private void createKarafPlatformResources(final IProgressMonitor monitor) throws CoreException {
        newKarafProject.getProjectHandle().getFolder(".bin").create(true, true, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform").create(true, true, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/etc").createLink(workingPlatformModel.getParentKarafModel().getConfigurationDirectory(), 0, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/deploy").createLink(workingPlatformModel.getParentKarafModel().getUserDeployedDirectory(), 0, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/lib").createLink(workingPlatformModel.getParentKarafModel().getRootDirectory().append("lib"), 0, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/system").createLink(workingPlatformModel.getParentKarafModel().getPluginRootDirectory(), 0, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/runtime").create(true, true, monitor);

        // TODO: Is this the right way to add the current installation?
        final IDynamicVariable eclipseHomeVariable = VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable("eclipse_home");
        final String eclipseHome = eclipseHomeVariable.getValue("");
        newKarafProject.getProjectHandle().getFolder(".bin/platform/eclipse").create(true, true, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/eclipse/dropins").createLink(new Path(eclipseHome).append("dropins"), 0, monitor);
        newKarafProject.getProjectHandle().getFolder(".bin/platform/eclipse/plugins").createLink(new Path(eclipseHome).append("plugins"), 0, monitor);

        newKarafProject.getProjectHandle().setPersistentProperty(
                new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafProject"),
                "true");

        newKarafProject.getProjectHandle().setPersistentProperty(
                new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafModel"),
                karafPlatformModel.getRootDirectory().toString());
    }

    private void createProject(final IProgressMonitor monitor) throws CoreException {
        final IProject project = newKarafProject.getProjectHandle();
        final IPath projectLocation = newKarafProject.getLocation();

        if (!Platform.getLocation().equals(projectLocation)) {
            final IProjectDescription projectDescription = project.getWorkspace().newProjectDescription(project.getName());
            projectDescription.setLocation(projectLocation);
            project.create(projectDescription, monitor);
        } else {
            project.create(monitor);
        }

        project.open(null);
    }

}
