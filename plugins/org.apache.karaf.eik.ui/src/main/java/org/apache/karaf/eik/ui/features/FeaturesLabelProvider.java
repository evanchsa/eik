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
package org.apache.karaf.eik.ui.features;

import org.apache.karaf.eik.core.features.Bundle;
import org.apache.karaf.eik.core.features.Feature;
import org.apache.karaf.eik.core.features.Features;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.model.ContentModel;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A {@link LabelProvider} and {@link ITableLabelProvider} suitable for
 * displaying Apache Karaf Features Repositories.
 */
public final class FeaturesLabelProvider extends LabelProvider implements ITableLabelProvider {

    public static final String MVN_URL_PREFIX = "mvn:";

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        switch (columnIndex) {
        case 0:
            return getImage(element);
        default:
            return null;
        }
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {

        switch (columnIndex) {
        case 0:
            return getText(element);
        case 1:
            if (element instanceof Feature) {
                final Feature feature = (Feature) element;
                return feature.getVersion();
            } else {
                return null;
            }
        default:
            return null;
        }
    }

    @Override
    public Image getImage(final Object element) {
        if (element instanceof ContentModel) {
            return ((ContentModel) element).getImage();
        } else if (element instanceof FeaturesRepository) {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.LOGO_16X16_IMG);
        } else if (element instanceof Feature) {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
        } else if (element instanceof Bundle) {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.BUNDLE_OBJ_IMG);
        } else {
            return null;
        }
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof FeaturesRepository) {
            final FeaturesRepository featuresRepository = (FeaturesRepository) element;
            if (featuresRepository.getFeatures().getName() != null) {
                return featuresRepository.getFeatures().getName();
            } else {
                return featuresRepository.getName();
            }
        } else if (element instanceof Features) {
            final Features features = (Features) element;
            if (features.getName() != null) {
                return features.getName();
            } else  if (features.getParent() != null) {
                return features.getParent().getName();
            } else {
                return null;
            }
        } else if (element instanceof Feature) {
            final Feature feature = (Feature) element;
            return feature.getName();
        } else if (element instanceof Bundle) {
            final Bundle bundle = (Bundle) element;
            final String label;
            if (bundle.getBundleUrl().startsWith(MVN_URL_PREFIX)) {
                final String[] bundleComponents = bundle.getBundleUrl().split("/"); //$NON-NLS-1$
                label = bundleComponents[1];
            } else {
                label = element.toString();
            }

            return label;
        } else {
            return element.toString();
        }
    }

}