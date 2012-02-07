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
package org.apache.karaf.eclipse.ui.project.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.PropertyUtils;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafRuntimePropertyBuildUnit extends AbstractKarafBuildUnit {

    /**
     *
     * @param karafPlatformModel
     * @param karafProject
     * @param projectBuilder
     */
    public KarafRuntimePropertyBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject, final IncrementalProjectBuilder projectBuilder) {
        super(karafPlatformModel, karafProject, projectBuilder);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final String karafHome = getKarafPlatformModel().getRootDirectory().toOSString();

        final Properties combinedProperties = new Properties();
        combinedProperties.put("karaf.home", karafHome);
        combinedProperties.put("karaf.base", karafHome);
        combinedProperties.put("karaf.data", getKarafPlatformModel().getRootDirectory().append("data").toOSString());

        for (final String filename : new String[] { "config.ini", "config.properties", "system.properties", "users.properties" }) {
            final File configFile = new File(getKarafPlatformModel().getConfigurationDirectory().toFile(), filename);
            if (configFile.exists()) {
                final Properties fileProperties =
                    KarafCorePluginUtils.loadProperties(getKarafPlatformModel().getConfigurationDirectory().toFile(), filename, true);
                combinedProperties.putAll(fileProperties);
            }
        }

        PropertyUtils.interpolateVariables(combinedProperties, combinedProperties);

        final IFolder runtimeFolder = getKarafProject().getFolder("runtime");
        if (!runtimeFolder.exists()) {
            runtimeFolder.create(true, true, monitor);
        }

        final IPath runtimeProperties =
            runtimeFolder.getRawLocation().append("runtime").addFileExtension("properties");

        runtimeFolder.refreshLocal(0, monitor);

        FileOutputStream out;
        try {
            out = new FileOutputStream(runtimeProperties.toFile());
            combinedProperties.store(out, "Combined interpolated runtime properties");
        } catch (final IOException e) {
            throw new CoreException(
                    new Status(
                            IStatus.ERROR,
                            KarafUIPluginActivator.PLUGIN_ID,
                            "Unable to build runtime property file",
                            e));
        }
    }
}
