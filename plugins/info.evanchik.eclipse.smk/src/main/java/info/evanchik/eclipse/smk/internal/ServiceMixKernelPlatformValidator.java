/**
 * Copyright (c) 2010 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
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
