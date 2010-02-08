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

    private static final ServiceMixKernelPlatformValidator platformValidator =
        new ServiceMixKernelPlatformValidator();

    /**
     *
     * @param platformPath
     */
    public ServiceMixKernelPlatformModel(IPath platformPath) {
        super(platformPath);
    }

    @Override
    public List<String> getBootClasspath() {
        final List<String> finalClasspath = new ArrayList<String>();

        final List<String> bootClasspath = super.getBootClasspath();
        for (String e : bootClasspath) {
            if (!e.toLowerCase().endsWith(SERVICEMIX_MAIN_JAR)) {
                finalClasspath.add(e);
            }
        }

        final Bundle b = Platform.getBundle(SERVICEMIX_MAIN_SPI_PROVIDER);

        if (b == null) {
            ServiceMixKernelActivator.getLogger().error("Unable to resolve MainService SPI bundle: " + SERVICEMIX_MAIN_SPI_PROVIDER);
        } else {
            try {
                final File location = FileLocator.getBundleFile(b);
                finalClasspath.add(location.getAbsolutePath());
            } catch(IOException e) {
                ServiceMixKernelActivator.getLogger().error("Unable to resolve MainService SPI bundle: " + SERVICEMIX_MAIN_SPI_PROVIDER, e);
            }
        }
        finalClasspath.add("/home/evanchsa/apps/plugins/org.apache.servicemix.kernel.main_1.1.0.201002071639.jar");

        return finalClasspath;
    }

    @Override
    public boolean isValid() {
        return platformValidator.isValid(getRootDirectory());
    }
}
