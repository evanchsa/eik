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
package org.apache.karaf.eclipse.ui.configuration.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eclipse.ui.configuration.FeaturesSection;
import org.eclipse.core.runtime.Path;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeaturesSectionImpl extends AbstractPropertiesConfigurationSection
        implements FeaturesSection {

    public static final String FEATURE_FILENAME = "org.apache.karaf.features.cfg"; // $NON-NLS-1$

    public static final String FEATURE_SECTION_ID = "org.apache.karaf.eclipse.configuration.section.Features"; // $NON-NLS-1$

    private static final String FEATURES_BOOT_KEY = "featuresBoot"; // $NON-NLS-1$

    private static final String FEATURES_REPOSITORIES_KEY = "featuresRepositories"; // $NON-NLS-1$

    /**
     *
     * @param parent
     */
    public FeaturesSectionImpl(final KarafPlatformModel parent) {
        super(FEATURE_SECTION_ID, new Path("etc").append(FEATURE_FILENAME), parent);
    }

    @Override
    public List<String> getBootFeatureNames() {
        final String rawBootFeatures = getProperties().getProperty(FEATURES_BOOT_KEY);
        if (rawBootFeatures == null || rawBootFeatures.trim().isEmpty()) {
            return Collections.emptyList();
        }

        final String[] bootFeatures = rawBootFeatures.split(","); // $NON-NLS-1$

        return Arrays.asList(bootFeatures);
    }

    @Override
    public List<String> getRepositoryList() {
        final String rawRepositories = (String) getProperties().get(FEATURES_REPOSITORIES_KEY);
        if (rawRepositories == null || rawRepositories.trim().isEmpty()) {
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
