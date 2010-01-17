/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformValidatorStrategy;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformValidator implements KarafPlatformValidatorStrategy {

    private final IPath rootPath;

    public GenericKarafPlatformValidator(IPath rootPath) {
        this.rootPath = rootPath;
    }

    public boolean isValid() {
        final IPath karafJar = rootPath.append("/lib/karaf.jar");
        final IPath servicemixJar = rootPath.append("/lib/servicemix.jar");

        final IPath systemDir = rootPath.append("/system");
        final IPath confDir = rootPath.append("/etc");

        // First level is the Karaf or ServiceMix JARs in the lib directory
        if (karafJar.toFile().exists() || servicemixJar.toFile().exists()) {
            return true;
        }

        /*
         * Second level is the directory structure with the features system
         * configuration files.
         */
        if(systemDir.toFile().isDirectory() && confDir.toFile().isDirectory()) {
            final IPath karafFeatures = confDir.append("/org.apache.felix.karaf.features.cfg");
            final IPath servicemixFeatures = confDir.append("/org.apache.servicemix.kernel.features.cfg");

            if(karafFeatures.toFile().exists() || servicemixFeatures.toFile().exists()) {
                return true;
            }
        }


        return false;
    }

}
