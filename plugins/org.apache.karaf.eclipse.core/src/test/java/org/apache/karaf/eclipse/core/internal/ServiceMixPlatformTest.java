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

import java.io.File;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafPlatformDetails;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.model.GenericKarafPlatformModel;
import org.apache.karaf.eclipse.core.test.KarafTestCase;
import org.apache.karaf.eclipse.core.test.TestUtils;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixPlatformTest extends KarafTestCase {

    @Test
    public void sanityCheckAllVersions() {
        for (final String version : serviceMixVersions) {
            final File karaf =  TestUtils.DEFAULT.getServiceMixDistribution(version);

            if (karaf == null) {
                Assert.fail("Could not find ServiceMix distribution at version: " + version);
            }

            final GenericKarafPlatformModel karafPlatform =
                new GenericKarafPlatformModel(new Path(karaf.getAbsolutePath()));

            Assert.assertTrue(karafPlatform.getBootClasspath().size() > 0);
        }
    }

    @Test
    public void testServiceMixPlatform_4_3_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getServiceMixPlatformModel("4.3.0");

        final List<String> bootClassPath = karafPlatformModel.getBootClasspath();

        Assert.assertEquals(3, bootClassPath.size());
    }

    @Test
    public void testServiceMixPlatformDetails_4_3_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getServiceMixPlatformModel("4.3.0");

        final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals("2.1.3", karafPlatformDetails.getVersion());
    }

    @Test
    public void testServiceMixPlatform_4_4_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getServiceMixPlatformModel("4.4.0");

        final List<String> bootClassPath = karafPlatformModel.getBootClasspath();

        Assert.assertEquals(3, bootClassPath.size());
    }

    @Test
    public void testServiceMixPlatformDetails_4_4_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getServiceMixPlatformModel("4.4.0");

        final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals("2.2.4", karafPlatformDetails.getVersion());
    }
}
