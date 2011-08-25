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
import info.evanchik.eclipse.karaf.ui.features.FeaturesResolverJob;
import info.evanchik.eclipse.karaf.ui.internal.PopulateObrFileJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

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
        } finally {
            monitor.done();
        }

        return null;
    }

    private void fullBuild(final IProgressMonitor monitor) throws CoreException {
        buildFeaturesRepositories(monitor);
        buildObr(monitor);
    }

    /**
     * @param monitor
     * @throws CoreException
     */
    private void buildFeaturesRepositories(final IProgressMonitor monitor)
            throws CoreException
    {
        final IProject project = getProject();

        monitor.subTask("Resolving Features Repository for Apache Karaf Project: " + project.getName());

        final IKarafProject karafProject = (IKarafProject) project.getAdapter(IKarafProject.class);
        final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);

        final FeaturesResolverJob job = new FeaturesResolverJob(project.getName(), featuresSection);
        job.schedule();
        try {
            job.join();

            final List<FeaturesRepository> featuresRepositories = job.getFeaturesRepositories();

            final IFolder folder = getProject().getFolder(new Path(".bin/features"));
            if (!folder.exists()) {
                folder.create(true, true, monitor);
            }

            for (final FeaturesRepository repo : featuresRepositories) {
                final File file = new File(folder.getRawLocation().append(repo.getName()).addFileExtension("xml").toString());

                final FileOutputStream fout = new FileOutputStream(file);
                repo.write(fout);
                fout.close();
            }

            monitor.worked(1);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buildObr(final IProgressMonitor monitor) throws CoreException {
        final IProject project = getProject();

        monitor.subTask("Creating OBR for Apache Karaf Project: " + project.getName());

        final IPath obrFile =
            project.getFolder(".bin/platform").getRawLocation().append("obr").addFileExtension("xml");

        final PopulateObrFileJob populateObrJob = new PopulateObrFileJob(project.getName(), obrFile.toFile());
        populateObrJob.schedule();

        try {
          populateObrJob.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        try {
            delta.accept(new IResourceDeltaVisitor() {
               @Override
            public boolean visit(final IResourceDelta delta) {
                  // if is a bundle file then schedule an update to the target platform file
                  // if something in eclipse home changes then update obr
                  return true;
               }
            });
         } catch (final CoreException e) {
            e.printStackTrace();
         }
    }
}
