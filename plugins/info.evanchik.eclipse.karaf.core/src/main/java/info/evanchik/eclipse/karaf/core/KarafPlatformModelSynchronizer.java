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
public interface KarafPlatformModelSynchronizer {

    /**
     *
     * @param dst
     * @param overwrite
     * @return
     */
    public IStatus synchronize(KarafPlatformModel dst, boolean overwrite);

    /**
     *
     * @param dst
     * @param fileKey
     * @param overwrite
     * @return
     */
    public IStatus synchronize(KarafPlatformModel dst, String fileKey, boolean overwrite);
}
