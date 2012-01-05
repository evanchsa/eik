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

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformResourcesBuildUnit extends AbstractKarafBuildUnit {

    /**
     *
     * @param karafPlatformModel
     * @param karafProject
     */
    public KarafPlatformResourcesBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final IKarafProject newKarafProject = getKarafProject();
        final IProject project = newKarafProject.getProjectHandle();

        for (final String folderName : new String[] { ".bin", ".bin/platform", ".bin/runtime" }) {
            final IFolder folder = project.getFolder(folderName);
            if (!folder.exists()) {
                folder.create(true, true, monitor);
            }
        }

        if (!project.getFolder(".bin/platform/etc").exists()) {
            project.getFolder(".bin/platform/etc").createLink(getKarafPlatformModel().getConfigurationDirectory(), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/deploy").exists()) {
            project.getFolder(".bin/platform/deploy").createLink(getKarafPlatformModel().getUserDeployedDirectory(), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/lib").exists()) {
            project.getFolder(".bin/platform/lib").createLink(getKarafPlatformModel().getRootDirectory().append("lib"), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/system").exists()) {
            project.getFolder(".bin/platform/system").createLink(getKarafPlatformModel().getPluginRootDirectory(), 0, monitor);
        }

        // TODO: Is this the right way to add the current installation?
        final IDynamicVariable eclipseHomeVariable = VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable("eclipse_home");
        final String eclipseHome = eclipseHomeVariable.getValue("");

        if (!project.getFolder(".bin/platform/eclipse").exists()) {
            project.getFolder(".bin/platform/eclipse").create(true, true, monitor);
        }

        if (!project.getFolder(".bin/platform/eclipse/dropins").exists()) {
            project.getFolder(".bin/platform/eclipse/dropins").createLink(new Path(eclipseHome).append("dropins"), 0, monitor);
        }

        if (!project.getFolder(".bin/platform/eclipse/plugins").exists()) {
            project.getFolder(".bin/platform/eclipse/plugins").createLink(new Path(eclipseHome).append("plugins"), 0, monitor);
        }
    }
}
