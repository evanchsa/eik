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
package org.apache.karaf.eclipse.ui.project;

import java.util.Map;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.project.impl.FeaturesRepositoriesBuildUnit;
import org.apache.karaf.eclipse.ui.project.impl.FilteredOsgiFrameworkJarBuildUnit;
import org.apache.karaf.eclipse.ui.project.impl.KarafRuntimePropertyBuildUnit;
import org.apache.karaf.eclipse.ui.project.impl.PopulateObrFileBuildUnit;
import org.apache.karaf.eclipse.ui.project.impl.TargetDefinitionBuildUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafProjectBuilder extends IncrementalProjectBuilder {

    public static final String ID = "org.apache.karaf.eclipse.ui.karafProjectBuilder";

    public KarafProjectBuilder() {
    }

    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor)
        throws CoreException
    {
        final IProject project = getProject();

        // Sentry to prevent builders from running
        if (getKarafProject() == null) {
        	// TODO: This should issue a warning in the Error Log
        	return null;
        }

        monitor.beginTask("Building Apache Karaf Project: " + project.getName(), 1);

        try {
            // TODO: Inject these somehow
            new FilteredOsgiFrameworkJarBuildUnit(getKarafPlatformModel(), getKarafProject(), this).build(kind, args, monitor);
            new KarafRuntimePropertyBuildUnit(getKarafPlatformModel(), getKarafProject(), this).build(kind, args, monitor);
            new FeaturesRepositoriesBuildUnit(getKarafPlatformModel(), getKarafProject(), this).build(kind, args, monitor);
            new PopulateObrFileBuildUnit(getKarafPlatformModel(), getKarafProject(), this).build(kind, args, monitor);
            new TargetDefinitionBuildUnit(getKarafPlatformModel(), getKarafProject(), this).build(kind, args, monitor);

            getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } finally {
            monitor.done();
        }

        return null;
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
}
