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
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.features.FeaturesResolverJob;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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

        final IKarafProject karafProject = (IKarafProject) project.getAdapter(IKarafProject.class);
        final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);

        final FeaturesResolverJob job = new FeaturesResolverJob(project.getName(), featuresSection);
        job.schedule();
        try {
            job.join();

            monitor.worked(1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.done();
        }

        return null;
    }

}
