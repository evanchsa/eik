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

import info.evanchik.eclipse.karaf.core.KarafPlatformValidator;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformValidator implements KarafPlatformValidator {

    public boolean isValid(IPath rootPath) {
        final IPath karafJar = rootPath.append("/lib/karaf.jar");

        final IPath systemDir = rootPath.append("/system");
        final IPath confDir = rootPath.append("/etc");

        // First level is the Karaf JARs in the lib directory
        if (karafJar.toFile().exists()) {
            return true;
        }

        /*
         * Second level is the directory structure with the features system
         * configuration files.
         */
        if(systemDir.toFile().isDirectory() && confDir.toFile().isDirectory()) {
            final IPath karafFeatures = confDir.append("/org.apache.felix.karaf.features.cfg");

            if(karafFeatures.toFile().exists()) {
                return true;
            }
        }


        return false;
    }

}
