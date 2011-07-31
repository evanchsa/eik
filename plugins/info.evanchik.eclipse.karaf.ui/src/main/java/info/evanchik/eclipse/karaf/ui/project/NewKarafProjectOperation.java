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
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.pde.internal.core.target.provisional.IBundleContainer;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;
import org.eclipse.pde.internal.core.target.provisional.ITargetHandle;
import org.eclipse.pde.internal.core.target.provisional.ITargetPlatformService;
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

    /**
     *
     * @param folder
     * @throws CoreException
     */
    public void createFolder(final IFolder folder) throws CoreException {
        if (!folder.exists()) {
            final IContainer parent = folder.getParent();

            if (parent instanceof IFolder) {
                createFolder((IFolder) parent);
            }

            folder.create(true, true, null);
        }
    }

    @Override
    protected void execute(final IProgressMonitor monitor)
        throws CoreException, InvocationTargetException, InterruptedException
    {
        monitor.beginTask("Creating Apache Karaf Project", 4);

        createProject(monitor);

        monitor.worked(1);

        addNatureToProject(KarafProjectNature.ID, monitor);

        monitor.worked(1);

        createTargetPlatform(monitor);

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
        newKarafProject.getProjectHandle().setPersistentProperty(new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafProject"), "true");
        newKarafProject.getProjectHandle().setPersistentProperty(new QualifiedName(KarafUIPluginActivator.PLUGIN_ID, "karafModel"), karafPlatformModel.getRootDirectory().toString());
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

    /**
     * @return
     */
    private File createTargetDefinitionFile() {
        final File targetLocation = workingPlatformModel.getRootDirectory().append(newKarafProject.getName()).addFileExtension("target").toFile();
        return targetLocation;
    }

    /**
     *
     * @param monitor
     * @throws CoreException
     */
    @SuppressWarnings("restriction")
    private void createTargetPlatform(final IProgressMonitor monitor) throws CoreException {
        final ITargetPlatformService targetPlatformService =
            (ITargetPlatformService) KarafUIPluginActivator.getDefault().getService(ITargetPlatformService.class.getName());

        final File targetLocation = createTargetDefinitionFile();
        final ITargetHandle targetHandle = targetPlatformService.getTarget(targetLocation.toURI());

        final ITargetDefinition target = targetHandle.getTargetDefinition();

        target.setName(newKarafProject.getName());

        final List<IBundleContainer> bundleContainers = KarafLaunchUtils.getBundleContainers(karafPlatformModel);
        target.setBundleContainers(bundleContainers.toArray(new IBundleContainer[0]));

        targetPlatformService.saveTargetDefinition(target);
    }
}