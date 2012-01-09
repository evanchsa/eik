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
package org.apache.karaf.eik.core.configuration.internal;

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eik.core.configuration.FeaturesSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeaturesSectionImpl extends AbstractPropertiesConfigurationSection
        implements FeaturesSection {

    public static final String FEATURE_FILENAME = "org.apache.karaf.features.cfg";

    public static final String FEATURE_SECTION_ID = "org.apache.karaf.eik.configuration.section.Features";

    private static final String FEATURES_BOOT_KEY = "featuresBoot";

    private static final String FEATURES_REPOSITORIES_KEY = "featuresRepositories";

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

        final String[] bootFeatures = rawBootFeatures.split(",");

        return Arrays.asList(bootFeatures);
    }

    @Override
    public List<String> getRepositoryList() {
        final String rawRepositories = (String) getProperties().get(FEATURES_REPOSITORIES_KEY);
        if (rawRepositories == null) {
            return Collections.emptyList();
        }

        final String[] repositories = rawRepositories.split(",");

        return Arrays.asList(repositories);
    }

    @Override
    public void setBootFeatureNames(final List<String> bootFeatures) {
        final String property = KarafCorePluginUtils.join(bootFeatures, ",");

        getProperties().setProperty(FEATURES_BOOT_KEY, property);
    }

    @Override
    public void setRepositoryList(final List<String> featuresRepositories) {
        final String property = KarafCorePluginUtils.join(featuresRepositories, ",");

        getProperties().setProperty(FEATURES_REPOSITORIES_KEY, property);
    }

}
