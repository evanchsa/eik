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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class KarafRuntimePropertyBuildUnit extends AbstractKarafBuildUnit {

    public KarafRuntimePropertyBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final String karafHome = getKarafPlatformModel().getRootDirectory().toOSString();

        final Properties combinedProperties = new Properties();
        combinedProperties.put("karaf.home", karafHome);
        combinedProperties.put("karaf.base", karafHome);
        // Add ref to karaf.etc for karaf-3.0.0
        combinedProperties.put("karaf.etc", getKarafPlatformModel().getRootDirectory().append("etc").toOSString());
        combinedProperties.put("karaf.data", getKarafPlatformModel().getRootDirectory().append("data").toOSString());

        for (final String filename : new String[] { "config.properties", "system.properties", "users.properties" }) {
            final Properties fileProperties =
                KarafCorePluginUtils.loadProperties(
                        getKarafPlatformModel().getConfigurationDirectory().toFile(),
                        filename,
                        true);

            combinedProperties.putAll(fileProperties);
        }

        PropertyUtils.interpolateVariables(combinedProperties, combinedProperties);

        final IFolder runtimeFolder = getKarafProject().getFolder("runtime");
        if (!runtimeFolder.exists()) {
            runtimeFolder.create(true, true, monitor);
        }

        final IPath runtimeProperties = runtimeFolder.getRawLocation().append("runtime").addFileExtension("properties");

        runtimeFolder.refreshLocal(0, monitor);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(runtimeProperties.toFile());
            combinedProperties.store(out, "Combined interpolated runtime properties");
        } catch (final IOException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to build runtime property file", e));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioException) {
                // ignore
            }
        }
    }

}
