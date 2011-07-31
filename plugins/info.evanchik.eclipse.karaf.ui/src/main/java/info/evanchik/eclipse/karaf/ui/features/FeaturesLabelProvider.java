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
package info.evanchik.eclipse.karaf.ui.features;

import info.evanchik.eclipse.karaf.core.features.Bundle;
import info.evanchik.eclipse.karaf.core.features.Feature;
import info.evanchik.eclipse.karaf.core.features.Features;
import info.evanchik.eclipse.karaf.core.features.FeaturesRepository;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class FeaturesLabelProvider extends LabelProvider implements ITableLabelProvider {

    public static final String MVN_URL_PREFIX = "mvn:"; //$NON-NLS-1$

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        switch (columnIndex) {
        case 0:
            if (element instanceof FeaturesRepository) {
                return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.LOGO_16X16_IMG);
            } else if (element instanceof Feature) {
                return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
            } else if (element instanceof Bundle) {
                return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.BUNDLE_OBJ_IMG);
            } else {
                return null;
            }

        default:
            return null;
        }
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {

        switch (columnIndex) {
        case 0:
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
}