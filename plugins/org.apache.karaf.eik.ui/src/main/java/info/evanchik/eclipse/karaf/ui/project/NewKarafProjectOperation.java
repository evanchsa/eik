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

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationInitializer;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class NewKarafProjectOperation extends WorkspaceModifyOperation {

    private final KarafPlatformModel karafPlatformModel;

    private final IKarafProject newKarafProject;

    private final KarafWorkingPlatformModel workingPlatformModel;

    /**
     *
     * @param karafPlatformModel
     * @param workingPlatformModel
     * @param newKarafProject
     */
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
        monitor.beginTask("Creating Apache Karaf Project", 3);

        createProject(monitor);

        monitor.worked(1);

        addNatureToProject(KarafProjectNature.ID, monitor);

        monitor.worked(1);

        createKarafPlatformResources(monitor);

        monitor.worked(1);

        newKarafProject.getProjectHandle().refreshLocal(2, monitor);

        monitor.done();
    }

    /**
     *
     * @param natureId
     * @param monitor
     * @throws CoreException
     */
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

    /**
     *
     * @param monitor
     * @throws CoreException
     */
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

        final ILaunchConfigurationType launchType =
            DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.pde.ui.EquinoxLauncher");

        final ILaunchConfigurationWorkingCopy launchConfiguration =
            launchType.newInstance(newKarafProject.getProjectHandle(), newKarafProject.getName());

        KarafLaunchConfigurationInitializer.initializeConfiguration(launchConfiguration);

        launchConfiguration.doSave();
    }

    /**
     *
     * @param monitor
     * @throws CoreException
     */
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
