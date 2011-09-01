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
package info.evanchik.eclipse.smk.internal;

import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelPlatformModel extends GenericKarafPlatformModel {

    private static final String SERVICEMIX_MAIN_JAR = "servicemix.jar";

    private static final String SERVICEMIX_MAIN_SPI_PROVIDER = "org.apache.servicemix.kernel.main";

    /**
     *
     * @param platformPath
     */
    public ServiceMixKernelPlatformModel(final IPath platformPath) {
        super(platformPath);
    }

    @Override
    public List<String> getBootClasspath() {
        final List<String> finalClasspath = new ArrayList<String>();

        final List<String> bootClasspath = super.getBootClasspath();
        for (final String e : bootClasspath) {
            if (!e.toLowerCase().endsWith(SERVICEMIX_MAIN_JAR)) {
                finalClasspath.add(e);
            }
        }

        final Bundle[] bundles = Platform.getBundles(SERVICEMIX_MAIN_SPI_PROVIDER, null);

        if (bundles == null) {
            ServiceMixKernelActivator.getLogger().error("Unable to resolve MainService SPI bundle: " + SERVICEMIX_MAIN_SPI_PROVIDER);
        } else {
            for (final Bundle b : bundles) {
                try {
                    final File location = FileLocator.getBundleFile(b);
                    if (!location.getAbsolutePath().endsWith(SERVICEMIX_MAIN_JAR)) {
                        finalClasspath.add(location.getAbsolutePath());
                        break;
                    }
                } catch(final IOException e) {
                    ServiceMixKernelActivator.getLogger().error("Unable to resolve MainService SPI bundle: " + SERVICEMIX_MAIN_SPI_PROVIDER, e);
                }
            }
        }

        return finalClasspath;
    }
}
