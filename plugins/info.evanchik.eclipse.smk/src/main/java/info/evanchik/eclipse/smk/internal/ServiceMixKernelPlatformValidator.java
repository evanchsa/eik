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

import info.evanchik.eclipse.karaf.core.KarafPlatformValidator;

import org.eclipse.core.runtime.IPath;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelPlatformValidator implements KarafPlatformValidator {

    private static final String SERVICEMIX_KEY_JAR = "servicemix.jar";

    private static final String SERVICEMIX_JAAS_KEY_JAR = "servicemix-jaas-boot.jar";

    private static final String FEATURES_CFG_FILE = "org.apache.servicemix.features.cfg";

    @Override
    public boolean isValid(IPath rootPath) {
        final IPath servicemixJar = rootPath.append("/lib/").append(SERVICEMIX_KEY_JAR);
        final IPath servicemixJaasJar = rootPath.append("/lib/").append(SERVICEMIX_JAAS_KEY_JAR);

        // First level is the ServiceMix JARs in the lib directory
        if (servicemixJar.toFile().exists() || servicemixJaasJar.toFile().exists()) {
            return true;
        }


        final IPath systemDir = rootPath.append("/system");
        final IPath confDir = rootPath.append("/etc");

        /*
         * Second level is the system directory structure and the features
         * configuration files.
         */
        if(systemDir.toFile().isDirectory() && confDir.toFile().isDirectory()) {
            final IPath servicemixFeatures = confDir.append("/").append(FEATURES_CFG_FILE);

            if(servicemixFeatures.toFile().exists()) {
                return true;
            }
        }


        return false;
    }
}
