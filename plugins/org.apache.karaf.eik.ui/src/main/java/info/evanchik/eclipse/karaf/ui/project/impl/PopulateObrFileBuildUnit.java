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

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.internal.PopulateObrFileJob;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class PopulateObrFileBuildUnit extends AbstractKarafBuildUnit {

    /**
     * @param karafPlatformModel
     * @param karafProject
     */
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
