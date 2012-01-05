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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ConfigurationFileBuildUnit extends AbstractKarafBuildUnit {

    /**
     *
     * @param karafPlatformModel
     * @param karafProject
     */
    public ConfigurationFileBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    /**
     *
     * @param kind
     * @param args
     * @param monitor
     */
    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {

        final File configurationDirectory = getKarafPlatformModel().getConfigurationDirectory().toFile();
        final File configFiles[] = configurationDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".cfg");
            }
        });

        final IFolder runtimeFolder = getKarafProject().getFolder("runtime");
        if (!runtimeFolder.exists()) {
            runtimeFolder.create(true, true, monitor);
        }

        final Properties configProperties = getKarafProject().getRuntimeProperties();

        for (final File f : configFiles) {
            if (f.isDirectory()) {
                continue;
            }

            try {
                final FileInputStream in = new FileInputStream(f);
            } catch (final FileNotFoundException e) {
            }

        }
    }
}
