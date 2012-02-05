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
import java.util.ArrayList;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.SystemBundleNames;
import org.apache.karaf.eclipse.core.test.KarafTestCase;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class StateBuilderTest extends KarafTestCase {

    @Test
    public void testStateBuilderBaseFunctionality() {
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel(LATEST_KNOWN_KARAF_VERSION);

        final ArrayList<File> jars = new ArrayList<File>();

        final File pluginRootDirectory = karafPlatformModel.getPluginRootDirectory().toFile();
        KarafCorePluginUtils.getJarFileList(pluginRootDirectory, jars, 50);

        final StateBuilder stateBuilder = new StateBuilder();
        stateBuilder.addAll(jars);

        final BundleDescription systemBundle = stateBuilder.getState().getBundle(SystemBundleNames.EQUINOX.toString(), null);
        final BundleDescription karafFeaturesCoreBundle = stateBuilder.getState().getBundle("org.apache.karaf.features.core", null);

        Assert.assertEquals(SystemBundleNames.EQUINOX.toString(), systemBundle.getSymbolicName());
        Assert.assertEquals("org.apache.karaf.features.core", karafFeaturesCoreBundle.getSymbolicName());
    }
}
