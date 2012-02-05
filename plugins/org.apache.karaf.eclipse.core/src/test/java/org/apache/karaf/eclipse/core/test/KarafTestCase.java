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
package org.apache.karaf.eclipse.core.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.model.GenericKarafPlatformModel;
import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class KarafTestCase {

    protected static final String LATEST_KNOWN_KARAF_VERSION = "2.2.5";

    protected static final String LATEST_KNOWN_SERVICEMIX_VERSION = "4.4.0";

    protected static final List<String> karafVersions = Arrays.asList("2.0.0", "2.1.0", "2.2.0", "2.2.1", "2.2.2", "2.2.4", "2.2.5");

    protected static final List<String> serviceMixVersions = Arrays.asList("4.3.0", "4.4.0");

    @BeforeClass
    public static final void initializeSystem() {
        TestUtils.DEFAULT.initializeSystemProperties();
    }

    /**
     *
     * @param version
     * @return
     */
    public KarafPlatformModel getKarafPlatformModel(final String version) {
        final File karaf = TestUtils.DEFAULT.getKarafDistribution(version);

        final GenericKarafPlatformModel karafPlatformModel =
            new GenericKarafPlatformModel(new Path(karaf.getAbsolutePath()));

        return karafPlatformModel;
    }

    /**
     *
     * @param version
     * @return
     */
    public KarafPlatformModel getServiceMixPlatformModel(final String version) {
        final File serviceMix = TestUtils.DEFAULT.getServiceMixDistribution(version);

        final GenericKarafPlatformModel karafPlatformModel =
            new GenericKarafPlatformModel(new Path(serviceMix.getAbsolutePath()));

        return karafPlatformModel;
    }
}
