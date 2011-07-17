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

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.wizards.NewKarafProjectWizard.NewKarafProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.target.DirectoryBundleContainer;
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

    public static final String KARAF_PROJECT_NATURE = "info.evanchik.eclipse.karaf.KarafProjectNature";

    private final KarafPlatformModel karafPlatformModel;

    private final NewKarafProject newKarafProject;

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
            final NewKarafProject newKarafProject)
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

        addNatureToProject(KARAF_PROJECT_NATURE, monitor);

        monitor.worked(1);

        createTargetPlatform(monitor);

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

    public void createFolder(final IFolder folder) throws CoreException {
        if (!folder.exists()) {
            final IContainer parent = folder.getParent();

            if (parent instanceof IFolder) {
                createFolder((IFolder) parent);
            }

            folder.create(true, true, null);
        }
    }

    private void createProject(final IProgressMonitor monitor) throws CoreException {
        final IProject project = newKarafProject.getProjectHandle();
        final IPath location = newKarafProject.getProjectLocation();

        if (!Platform.getLocation().equals(location)) {
            final IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
            desc.setLocation(location);
            project.create(desc, monitor);
        } else {
            project.create(monitor);
        }

        project.open(null);
    }

    @SuppressWarnings("restriction")
    private void createTargetPlatform(final IProgressMonitor monitor) throws CoreException {
        final ITargetPlatformService targetPlatformService =
            (ITargetPlatformService) KarafUIPluginActivator.getDefault().getService(ITargetPlatformService.class.getName());

        final File targetLocation = workingPlatformModel.getRootDirectory().append(newKarafProject.getProjectName()).addFileExtension("target").toFile();
        final ITargetHandle targetHandle = targetPlatformService.getTarget(targetLocation.toURI());

        final ITargetDefinition target = targetHandle.getTargetDefinition();

        target.setName(newKarafProject.getProjectName());

        final List<IBundleContainer> bundleContainers = getBundleContainers(karafPlatformModel);
        target.setBundleContainers(bundleContainers.toArray(new IBundleContainer[0]));

        targetPlatformService.saveTargetDefinition(target);
    }

    @SuppressWarnings("restriction")
    private List<IBundleContainer> getBundleContainers(final KarafPlatformModel karafPlatformModel) {
        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(
                karafPlatformModel.getPluginRootDirectory().toFile(),
                jarFiles,
                100);

        KarafCorePluginUtils.getJarFileList(
                karafPlatformModel.getUserDeployedDirectory().toFile(),
                jarFiles,
                100);

        // Add each JAR file's directory to the list of directories that contain
        // plugins
        final Set<File> directories = new HashSet<File>();
        for (final File f : jarFiles) {
            directories.add(f.getParentFile());
        }

        // Add the lib directory for completeness, if the developer is pulling a
        // target platform they better know what the are doing
        directories.add(karafPlatformModel.getRootDirectory().append("lib").toFile()); // $NON-NLS-1$

        final List<IBundleContainer> bundleContainers = new ArrayList<IBundleContainer>();
        for (final File dir : directories) {
            bundleContainers.add(new DirectoryBundleContainer(dir.getAbsolutePath()));
        }
        return bundleContainers;
    }
}
