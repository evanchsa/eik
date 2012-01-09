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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

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

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
