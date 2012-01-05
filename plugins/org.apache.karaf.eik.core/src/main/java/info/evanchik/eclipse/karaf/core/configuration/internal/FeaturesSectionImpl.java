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
package info.evanchik.eclipse.karaf.core.configuration.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.AbstractPropertiesConfigurationSection;
import info.evanchik.eclipse.karaf.core.configuration.FeaturesSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeaturesSectionImpl extends AbstractPropertiesConfigurationSection
        implements FeaturesSection {

    public static final String FEATURE_FILENAME = "org.apache.karaf.features.cfg"; // $NON-NLS-1$

    public static final String FEATURE_SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.Features"; // $NON-NLS-1$

    private static final String FEATURES_BOOT_KEY = "featuresBoot"; // $NON-NLS-1$

    private static final String FEATURES_REPOSITORIES_KEY = "featuresRepositories"; // $NON-NLS-1$

    /**
     *
     * @param parent
     */
    public FeaturesSectionImpl(final KarafPlatformModel parent) {
        super(FEATURE_SECTION_ID, FEATURE_FILENAME, parent);
    }

    @Override
    public List<String> getBootFeatureNames() {
        final String rawBootFeatures = getProperties().getProperty(FEATURES_BOOT_KEY);
        if (rawBootFeatures == null) {
            return Collections.emptyList();
        }

        final String[] bootFeatures = rawBootFeatures.split(","); // $NON-NLS-1$

        return Arrays.asList(bootFeatures);
    }

    @Override
    public List<String> getRepositoryList() {
        final String rawRepositories = (String) getProperties().get(FEATURES_REPOSITORIES_KEY);
        if (rawRepositories == null) {
            return Collections.emptyList();
        }

        final String[] repositories = rawRepositories.split(","); // $NON-NLS-1$

        return Arrays.asList(repositories);
    }

    @Override
    public void setBootFeatureNames(final List<String> bootFeatures) {
        final String property = KarafCorePluginUtils.join(bootFeatures, ","); // $NON-NLS-1$

        getProperties().setProperty(FEATURES_BOOT_KEY, property);
    }

    @Override
    public void setRepositoryList(final List<String> featuresRepositories) {
        final String property = KarafCorePluginUtils.join(featuresRepositories, ","); // $NON-NLS-1$

        getProperties().setProperty(FEATURES_REPOSITORIES_KEY, property);
    }
}
