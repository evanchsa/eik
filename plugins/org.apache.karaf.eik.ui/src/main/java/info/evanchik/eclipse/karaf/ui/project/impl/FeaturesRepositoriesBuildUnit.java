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
package info.evanchik.eclipse.karaf.ui.project.impl;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.features.FeaturesResolverJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeaturesRepositoriesBuildUnit extends AbstractKarafBuildUnit {

    /**
     * @param karafPlatformModel
     * @param karafProject
     */
    public FeaturesRepositoriesBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final IKarafProject karafProject = getKarafProject();
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel();

        monitor.subTask("Resolving Features Repository for Apache Karaf Project: " + karafProject.getName());

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        final FeaturesResolverJob job = new FeaturesResolverJob(karafProject.getName(), karafPlatformModel, featuresSection);
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
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to build Features Repository", e));
        } catch (final InterruptedException e) {
            Thread.interrupted();
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to build Features Repository", e));
        }
    }
}
