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
import info.evanchik.eclipse.karaf.core.configuration.FeaturesSection;
import info.evanchik.eclipse.karaf.core.features.FeaturesRepository;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.features.FeaturesResolverJob;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchUtils;
import info.evanchik.eclipse.karaf.ui.internal.PopulateObrFileJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
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
public class KarafProjectBuilder extends IncrementalProjectBuilder {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.karafProjectBuilder";

    public KarafProjectBuilder() {
    }

    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor)
        throws CoreException
    {
        final IProject project = getProject();

        monitor.beginTask("Building Apache Karaf Project: " + project.getName(), 1);

        try {
            if (kind == IncrementalProjectBuilder.FULL_BUILD) {
                fullBuild(monitor);
            } else {
                final IResourceDelta delta = getDelta(getProject());
                if (delta == null) {
                    fullBuild(monitor);
                } else {
                    incrementalBuild(delta, monitor);
                }
            }

            getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } finally {
            monitor.done();
        }

        return null;
    }

    /**
     * Performs a full build of this {@link IKarafProject}
     *
     * @param monitor
     * @throws CoreException
     */
    private void fullBuild(final IProgressMonitor monitor) throws CoreException {
        createTargetPlatform(monitor);
        buildFeaturesRepositories(monitor);
        buildObr(monitor);
    }

    /**
     * Build the Apache Karaf features repository files in this
     * {@link IKarafProject}'s project location
     *
     * @param monitor
     * @throws CoreException
     */
    private void buildFeaturesRepositories(final IProgressMonitor monitor)
            throws CoreException
    {
        final IKarafProject karafProject = getKarafProject();
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel();

        monitor.subTask("Resolving Features Repository for Apache Karaf Project: " + karafProject.getName());

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        final FeaturesResolverJob job = new FeaturesResolverJob(karafProject.getName(), featuresSection);
        job.schedule();
        try {
            job.join();

            final List<FeaturesRepository> featuresRepositories = job.getFeaturesRepositories();

            final IFolder folder = getKarafProject().getFolder("features");
            if (!folder.exists()) {
                folder.create(true, true, monitor);
            }

            for (final FeaturesRepository repo : featuresRepositories) {
                final IPath featuresRepositoryFilename = new Path(repo.getName()).addFileExtension("xml");
                final IFile featuresRepositoryFile = folder.getFile(featuresRepositoryFilename.toOSString());
                final File file = new File(featuresRepositoryFile.getRawLocation().toOSString());

                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(file);
                    repo.write(fout);
                } finally {
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (final IOException e) {
                            // This space left blank
                        }
                    }
                }
            }

            monitor.worked(1);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds the OBR file for the installed Eclipse platform in this
     * {@link IKarafProject}'s project location
     *
     * @param monitor
     * @throws CoreException
     */
    private void buildObr(final IProgressMonitor monitor) throws CoreException {
        final IKarafProject karafProject = getKarafProject();

        monitor.subTask("Creating OBR for Apache Karaf Project: " + karafProject.getName());

        final IPath obrFile =
            karafProject.getFolder("platform").getRawLocation().append("eclipse.obr").addFileExtension("xml");

        final PopulateObrFileJob populateObrJob = new PopulateObrFileJob(karafProject.getName(), obrFile.toFile());
        populateObrJob.schedule();

        try {
          populateObrJob.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
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
    @SuppressWarnings("restriction")
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

    /**
     * Getter for the {@link IKarafProject} of this builder's {@link IProject}
     *
     * @return the {@code IKarafProject}
     */
    private IKarafProject getKarafProject() {
        final IProject project = getProject();
        return (IKarafProject) project.getAdapter(IKarafProject.class);
    }

    /**
     * Getter for the {@link KarafPlatformModel} that this builder's
     * {@link IProject} is bound to
     *
     * @return the {@code KarafPlatformModel} that this builder's
     *         {@code IProject} is bound to
     */
    private KarafPlatformModel getKarafPlatformModel() {
        return (KarafPlatformModel) getKarafProject().getAdapter(KarafPlatformModel.class);
    }

    /**
     * Performs an incremental build of this {@link IKarafProject}
     *
     * @param delta
     * @param monitor
     * @throws CoreException
     */
    private void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        try {
            delta.accept(new IResourceDeltaVisitor() {
               @Override
            public boolean visit(final IResourceDelta delta) {
                  // if is a bundle file then schedule an update to the target platform file
                  // if something in eclipse home changes then update obr
                  // if it is a feature file update features repo
                  return true;
               }
            });
            fullBuild(monitor);
         } catch (final CoreException e) {
            e.printStackTrace();
         }
    }
}
