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
package org.apache.karaf.eclipse.ui.features;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.core.features.XmlFeaturesRepository;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeatureUtils {

    /**
     *
     * @return
     */
    public static FeatureUtils getDefault() {
        return new FeatureUtils();
    }

    /**
     *
     * @param karafPlatformModel
     * @return
     */
    public List<FeaturesRepository> getFeatureRepository(final KarafPlatformModel karafPlatformModel) throws CoreException {
        final List<FeaturesRepository> featuresRepositories = new ArrayList<FeaturesRepository>();

        final IKarafProject karafProject =
            (IKarafProject) Platform.getAdapterManager().getAdapter(karafPlatformModel, IKarafProject.class);

        final IFolder featuresFolder = karafProject.getFolder("features");

        if (!featuresFolder.exists()) {
            return Collections.emptyList();
        }

        FileInputStream fin = null;

        final IResource[] resources = featuresFolder.members();
        for (final IResource resource : resources) {
            if (resource.getFileExtension().equalsIgnoreCase("xml")) {
                try {
                    fin = new FileInputStream(resource.getRawLocation().toFile());
                    final XmlFeaturesRepository xmlFeatureRepository = new XmlFeaturesRepository(resource.getName(), fin);
                    featuresRepositories.add(xmlFeatureRepository);
                } catch (final IOException e) {
                    KarafUIPluginActivator.getLogger().warn("Uable to load feature repository", e);
                } finally {
                    if (fin != null) {
                        try {
                            fin.close();
                        } catch (final IOException e) {
                            // Intetionally left blank
                        }
                    }
                }
            }
        }

        return featuresRepositories;
    }
}
