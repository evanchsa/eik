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
package info.evanchik.eclipse.smk;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelFactory;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelSynchronizer;
import info.evanchik.eclipse.karaf.core.KarafPlatformValidator;
import info.evanchik.eclipse.smk.internal.ServiceMixKernelPlatformModel;
import info.evanchik.eclipse.smk.internal.ServiceMixKernelPlatformModelSynchronizer;
import info.evanchik.eclipse.smk.internal.ServiceMixKernelPlatformValidator;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelPlatformModelFactory implements KarafPlatformModelFactory {

    private static final ServiceMixKernelPlatformValidator platformValidator =
        new ServiceMixKernelPlatformValidator();

    @Override
    public KarafPlatformModel getPlatformModel(IPath rootDirectory) {
        if (!platformValidator.isValid(rootDirectory)) {

        }

        return new ServiceMixKernelPlatformModel(rootDirectory);
    }

    @Override
    public KarafPlatformModelSynchronizer getPlatformSynchronizer(KarafPlatformModel platformModel) {
        return new ServiceMixKernelPlatformModelSynchronizer(platformModel);
    }

    @Override
    public KarafPlatformValidator getPlatformValidator() {
        return platformValidator;
    }

}
