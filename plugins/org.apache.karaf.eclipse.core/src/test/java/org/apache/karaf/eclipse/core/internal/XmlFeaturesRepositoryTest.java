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
package org.apache.karaf.eclipse.core.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import junit.framework.Assert;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.features.Feature;
import org.apache.karaf.eclipse.core.features.FeatureResolverImpl;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.core.features.XmlFeaturesRepository;
import org.apache.karaf.eclipse.core.test.KarafTestCase;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class XmlFeaturesRepositoryTest extends KarafTestCase {

    private static final String LATEST_STANDARD_FEATURES_DESCRIPTOR = "org/apache/karaf/assemblies/features/standard/" + LATEST_KNOWN_KARAF_VERSION + "/standard-" + LATEST_KNOWN_KARAF_VERSION + "-features.xml";

    private static final String LATEST_ENTERPRISE_FEATURES_DESCRIPTOR = "org/apache/karaf/assemblies/features/enterprise/" + LATEST_KNOWN_KARAF_VERSION + "/enterprise-" + LATEST_KNOWN_KARAF_VERSION + "-features.xml";

    @Test
    public void testStandardFeaturesRepository() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel(LATEST_KNOWN_KARAF_VERSION);
        final IPath standardFeaturesRepository =
            karafPlatformModel.getPluginRootDirectory().append(LATEST_STANDARD_FEATURES_DESCRIPTOR);

        final FileInputStream fin = new FileInputStream(standardFeaturesRepository.toFile());
        try {
            final XmlFeaturesRepository featuresRepository = new XmlFeaturesRepository("standard", fin);

            Assert.assertEquals("standard", featuresRepository.getName());
            Assert.assertEquals(0, featuresRepository.getRepositories().size());
            Assert.assertEquals("karaf-" + LATEST_KNOWN_KARAF_VERSION, featuresRepository.getFeatures().getName());
            Assert.assertEquals(30, featuresRepository.getFeatures().getFeatures().size());

        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }
        }
    }

    @Test
    public void testEnterpriseFeaturesRepository() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel(LATEST_KNOWN_KARAF_VERSION);
        final IPath standardFeaturesRepository =
            karafPlatformModel.getPluginRootDirectory().append(LATEST_ENTERPRISE_FEATURES_DESCRIPTOR);

        final FileInputStream fin = new FileInputStream(standardFeaturesRepository.toFile());
        try {
            final XmlFeaturesRepository featuresRepository = new XmlFeaturesRepository("enterprise", fin);

            Assert.assertEquals("enterprise", featuresRepository.getName());
            Assert.assertEquals(0, featuresRepository.getRepositories().size());
            Assert.assertEquals("karaf-enterprise-" + LATEST_KNOWN_KARAF_VERSION, featuresRepository.getFeatures().getName());
            Assert.assertEquals(4, featuresRepository.getFeatures().getFeatures().size());
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }
        }
    }

    @Test
    public void testKarafFeatureResolver() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel(LATEST_KNOWN_KARAF_VERSION);
        final IPath standardFeaturesRepository =
            karafPlatformModel.getPluginRootDirectory().append(LATEST_STANDARD_FEATURES_DESCRIPTOR);

        final FileInputStream fin = new FileInputStream(standardFeaturesRepository.toFile());
        try {
            final FeaturesRepository featuresRepository = new XmlFeaturesRepository("standard", fin);

            final FeatureResolverImpl featureResolver = new FeatureResolverImpl(Collections.singletonList(featuresRepository));

            final Feature karafFramework = featureResolver.findFeature("karaf-framework");

            Assert.assertNotNull(karafFramework);
            Assert.assertEquals(LATEST_KNOWN_KARAF_VERSION, karafFramework.getVersion());
            Assert.assertEquals(25, karafFramework.getBundles().size());
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }
        }
    }
}
