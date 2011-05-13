/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.ui.KarafLaunchConfigurationConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafLaunchConfigurationAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {
        final Object adapted;
        if (     KarafPlatformModel.class.equals(adapterType)
              && adaptableObject instanceof ILaunchConfiguration)
        {

            final ILaunchConfiguration configuration = (ILaunchConfiguration) adaptableObject;
            try {
                if (configuration.getAttributes().containsKey(KarafLaunchConfigurationConstants.KARAF_LAUNCH_SOURCE_RUNTIME)) {
                    final String platformPath = (String) configuration.getAttributes().get(KarafLaunchConfigurationConstants.KARAF_LAUNCH_SOURCE_RUNTIME);
                    adapted = KarafPlatformModelRegistry.findPlatformModel(new Path(platformPath));
                } else {
                    adapted = null;
                }
            } catch (final CoreException e) {
                throw new IllegalStateException(e);
            }
        } else {
            adapted = null;
        }

        return adapted;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { KarafPlatformModel.class };
    }
}
