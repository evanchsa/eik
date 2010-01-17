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
package info.evanchik.eclipse.karaf.core.model;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafModelSynchronizer;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class DefaultKarafModelSynchronizer implements KarafModelSynchronizer {

    private static final DefaultKarafModelSynchronizer instance = new DefaultKarafModelSynchronizer();

    /**
     * Helper method that allows for easy access to the single method.
     *
     * @return the singleton instance for this {@link KarafModelSynchronizer}
     */
    public static KarafModelSynchronizer getInstance() {
        return instance;
    }

    public IStatus synchronize(KarafPlatformModel src, KarafPlatformModel dst, String fileKey) {
        if (dst.isReadOnly()) {
            throw new IllegalArgumentException(
                            "Destination Karaf platform model must not be read only");
        }

        final IPath srcFile = src.getConfigurationFile(fileKey);
        final IPath dstFile = dst.getConfigurationFile(fileKey);

        if (srcFile == null || dstFile == null) {
            KarafCorePluginActivator.getLogger().error(
                            "File key is not valid for source and destination platform model: "
                                            + fileKey);
        }

        try {
            KarafCorePluginUtils.copyFile(srcFile.toFile(), dstFile.toFile());
        } catch (IOException e) {
            KarafCorePluginActivator.getLogger().error(
                            "Unable to copy file " + srcFile.toOSString() + " to "
                                            + dstFile.toOSString(), e);
        }

        return Status.OK_STATUS;
    }
}
