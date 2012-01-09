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
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.internal.PopulateObrFileJob;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PopulateObrFileBuildUnit extends AbstractKarafBuildUnit {

    public PopulateObrFileBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final IKarafProject karafProject = getKarafProject();

        monitor.subTask("Creating OBR for Apache Karaf Project: " + karafProject.getName());

        final IFolder platformFolder = karafProject.getFolder("platform");

        final IPath obrFile =
            platformFolder.getRawLocation().append("eclipse.obr").addFileExtension("xml");

        platformFolder.refreshLocal(0, monitor);

        final PopulateObrFileJob populateObrJob = new PopulateObrFileJob(karafProject.getName(), obrFile.toFile());
        populateObrJob.schedule();

        try {
          populateObrJob.join();
        } catch (final InterruptedException e) {
            Thread.interrupted();
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to populate OBR file", e));
        }
    }

}
