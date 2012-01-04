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
package info.evanchik.eclipse.karaf.core;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafPlatformDetails {

    /**
     * Retrieves the description of this Karaf platform
     *
     * @return the description of the Karaf platform
     */
    public String getDescription();

    /**
     * Retrieves the name of this Karaf platform
     *
     * @return the name of the Karaf platform
     */
    public String getName();

    /**
     * Retrieves the version of this Karaf platform
     *
     * @return the version of the Karaf platform
     */
    public String getVersion();
}
