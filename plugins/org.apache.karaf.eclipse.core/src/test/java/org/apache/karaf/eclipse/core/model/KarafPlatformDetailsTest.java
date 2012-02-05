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
package org.apache.karaf.eclipse.core.model;

import java.util.List;

import junit.framework.Assert;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.test.KarafTestCase;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformDetailsTest extends KarafTestCase {

    @Test
    public void testKarafPlatformDetailsFactoryMethod() throws Exception {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel(LATEST_KNOWN_KARAF_VERSION);

        IPath jarFilePath = null;

        final List<String> bootClasspath = karafPlatformModel.getBootClasspath();
        for (final String karafJar : bootClasspath) {
            if (karafJar.endsWith("karaf.jar")) {
                jarFilePath = new Path(karafJar);
                break;
            }
        }

        final GenericKarafPlatformDetails karafPlatformDetails = GenericKarafPlatformDetails.create(jarFilePath);

        Assert.assertEquals("Apache Karaf", karafPlatformDetails.getName());
        Assert.assertEquals("OSGi R4 framework.", karafPlatformDetails.getDescription());
        Assert.assertEquals(LATEST_KNOWN_KARAF_VERSION, karafPlatformDetails.getVersion());
    }
}
