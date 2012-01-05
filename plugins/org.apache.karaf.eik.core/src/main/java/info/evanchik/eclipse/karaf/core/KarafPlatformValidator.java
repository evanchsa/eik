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

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafPlatformValidator {

    /**
     * Determines if the Karaf platform model is valid and complete.
     *
     * @param rootPath
     *            the root path of the platform model that will be evaluated
     * @return true if the platform is valid, false otherwise
     */
    public boolean isValid(IPath rootPath);
}
