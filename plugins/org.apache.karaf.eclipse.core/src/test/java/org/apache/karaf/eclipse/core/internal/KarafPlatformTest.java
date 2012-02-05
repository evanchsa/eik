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
public class KarafPlatformTest extends KarafTestCase {

    @Test
    public void sanityCheckAllVersions() {
        for (final String version : karafVersions) {
            final File karaf =  TestUtils.DEFAULT.getKarafDistribution(version);

            if (karaf == null) {
                Assert.fail("Could not find Karaf distribution at version: " + version);
            }

            final GenericKarafPlatformModel karafPlatform =
                new GenericKarafPlatformModel(new Path(karaf.getAbsolutePath()));

            Assert.assertTrue(karafPlatform.getBootClasspath().size() > 0);
        }
    }

    @Test
    public void testKarafPlatform_2_0_0() throws Exception {
        final KarafPlatformModel karafPlatform_2_0_0 = getKarafPlatformModel("2.0.0");

        final List<String> bootClassPath = karafPlatform_2_0_0.getBootClasspath();

        Assert.assertEquals(3, bootClassPath.size());
    }

    @Test
    public void testKarafPlatformDetails_2_0_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel("2.0.0");

        final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals("2.0.0", karafPlatformDetails.getVersion());
    }

    @Test
    public void testKarafPlatform_2_1_0() throws Exception {
        final KarafPlatformModel karafPlatform_2_1_0 = getKarafPlatformModel("2.1.0");

        final List<String> bootClassPath = karafPlatform_2_1_0.getBootClasspath();

        Assert.assertEquals(3, bootClassPath.size());
    }

    @Test
    public void testKarafPlatformDetails_2_1_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel("2.1.0");

        final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals("2.1.0", karafPlatformDetails.getVersion());
    }

    @Test
    public void testKarafPlatform_2_2_0() throws Exception {
        final KarafPlatformModel karafPlatform_2_2_0 = getKarafPlatformModel("2.2.0");

        final List<String> bootClassPath = karafPlatform_2_2_0.getBootClasspath();

        Assert.assertEquals(2, bootClassPath.size());
    }

    @Test
    public void testKarafPlatformDetails_2_2_0() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel("2.2.0");

        final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals("2.2.0", karafPlatformDetails.getVersion());
    }
}
