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
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelSynchronizer;
import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformModelSynchronizer implements KarafPlatformModelSynchronizer {

    private static final List<String> fileKeys;

    static {
        final List<String> tempFileKeys = new ArrayList<String>();

        tempFileKeys.add("java.util.logging.properties"); //$NON-NLS-1$
        tempFileKeys.add("jre.properties"); //$NON-NLS-1$
        tempFileKeys.add("config.properties"); //$NON-NLS-1$
        tempFileKeys.add("custom.properties"); //$NON-NLS-1$
        tempFileKeys.add("system.properties"); //$NON-NLS-1$
        tempFileKeys.add("startup.properties"); //$NON-NLS-1$
        tempFileKeys.add("org.ops4j.pax.logging.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.ops4j.pax.url.mvn.cfg"); //$NON-NLS-1$

        fileKeys = Collections.unmodifiableList(tempFileKeys);
    }

    protected final KarafPlatformModel platformModel;

    public GenericKarafPlatformModelSynchronizer(KarafPlatformModel platformModel) {
        this.platformModel = platformModel;
    }

    public IStatus synchronize(KarafPlatformModel dst, boolean overwrite) {

        for (String key : getFileKeys()) {
            final IStatus status = synchronize(platformModel, dst, key, overwrite);
            if (!status.equals(Status.OK_STATUS)) {
                // Abort if there is an error
                return status;
            }
        }

        return Status.OK_STATUS;
    }

    public IStatus synchronize(KarafPlatformModel dst, String fileKey, boolean overwrite) {
        return synchronize(platformModel, dst, fileKey, overwrite);
    }

    protected List<String> getFileKeys() {
        return fileKeys;
    }

    /**
     * Synchronizes the destination {@link KarafPlatformModel} with the source
     * {@code KarafPlatformModel}.<br>
     * <br>
     * The destination {@code KarafPlatformModel} <b>MUST NOT</b> read-only.
     *
     * @param src
     *            the source {@code KarafPlatformModel}
     * @param dst
     *            the destination {@code KarafPlatformModel}
     * @param fileKey
     *            the key indicating which file to synchronize (see
     *            {@link KarafPlatformModel#getConfigurationFile(String)})
     * @param overwrite
     *            if true the files will be overwritten should they exist, a
     *            value of false will preserve existing file contents
     * @return if successfully a {@code IStatus} derived from {@link IStatus#OK}
     *         ; if unsuccessful a {@code IStatus} derived from
     *         {@link IStatus#ERROR}
     */
    private IStatus synchronize(KarafPlatformModel src, KarafPlatformModel dst, String fileKey, boolean overwrite) {
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
            if (!overwrite && dstFile.toFile().exists()) {
                return Status.OK_STATUS;
            }

            if (!srcFile.toFile().exists()) {
                // TODO: What do do here?
                return Status.OK_STATUS;
            }

            KarafCorePluginUtils.copyFile(srcFile.toFile(), dstFile.toFile());
        } catch (IOException e) {
            KarafCorePluginActivator.getLogger().error(
                            "Unable to copy file " + srcFile.toOSString() + " to "
                                            + dstFile.toOSString(), e);
        }

        return Status.OK_STATUS;
    }
}
