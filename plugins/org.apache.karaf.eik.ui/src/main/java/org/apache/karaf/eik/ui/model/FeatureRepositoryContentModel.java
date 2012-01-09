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
package org.apache.karaf.eik.ui.model;

import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.core.features.XmlFeaturesRepository;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public final class FeatureRepositoryContentModel extends AbstractContentModel {

    private final Set<FeaturesRepository> featuresRepository = new HashSet<FeaturesRepository>();

    public FeatureRepositoryContentModel(final IKarafProject project) {
        super(project);
    }

    @Override
    public Object[] getElements() {
        FileInputStream fin = null;
        try {
            final IFolder featuresFolder = project.getFolder("features");
            if (!featuresFolder.exists()) {
                return new Object[0];
            }

            final IResource[] resources = featuresFolder.members();

            for (final IResource featureFileResource : resources) {
                if (featureFileResource.getFullPath().getFileExtension().equals("xml")) {
                    fin = new FileInputStream(featureFileResource.getRawLocation().toFile());
                    featuresRepository.add(new XmlFeaturesRepository(featureFileResource.getName(), fin));
                    fin.close();
                } else {
                    // TODO: What to do here?
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final CoreException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // This is intentionally left blank
                }
            }
        }

        return featuresRepository.toArray();
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
    }

    @Override
    public String toString() {
        return "Feature Repositories";
    }

}