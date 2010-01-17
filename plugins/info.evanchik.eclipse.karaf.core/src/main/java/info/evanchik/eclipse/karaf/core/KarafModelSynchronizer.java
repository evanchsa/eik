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
package info.evanchik.eclipse.karaf.core;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafModelSynchronizer {

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
     * @return if successfully a {@code IStatus} derived from {@link IStatus#OK}
     *         ; if unsuccessful a {@code IStatus} derived from
     *         {@link IStatus#ERROR}
     */
    public IStatus synchronize(KarafPlatformModel src, KarafPlatformModel dst, String fileKey);
}
