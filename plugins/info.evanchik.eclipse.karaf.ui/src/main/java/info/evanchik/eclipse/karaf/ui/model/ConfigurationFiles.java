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
package info.evanchik.eclipse.karaf.ui.model;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ConfigurationFiles extends AbstractContentModel {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private static final class FeatureRepositoryContentModel extends AbstractContentModel {

        /**
         *
         * @param project
         * @param karafPlatformModel
         * @param featuresFile
         */
        public FeatureRepositoryContentModel(final IProject project, final KarafPlatformModel karafPlatformModel, final File featuresFile) {
            super(project, karafPlatformModel);
        }

        @Override
        public Object[] getElements() {
            return new Object[0];
        }

        @Override
        public Image getImage() {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
        }

        @Override
        public String toString() {
            return "Feature Repositories";
        }
    };

    /**
     *
     * @param project
     * @param karafPlatformModel
     */
    public ConfigurationFiles(final IProject project, final KarafPlatformModel karafPlatformModel) {
        super(project, karafPlatformModel);
    }

    @Override
    public Object[] getElements() {
        final List<Object> elements = new ArrayList<Object>();

        final File[] configFiles = karafPlatformModel.getConfigurationDirectory().toFile().listFiles();
        for (final File f : configFiles) {
            if (f.getName().equals("org.apache.karaf.features.cfg")) {
                elements.add(new FeatureRepositoryContentModel(project, karafPlatformModel, f));
            } else {
                // Do not add any files until there is a ContentModel for them
                // elements.add(f);
            }
        }
        return elements.toArray();
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get("details_view");
    }

    @Override
    public String toString() {
        return "Configuration Files";
    }
}
