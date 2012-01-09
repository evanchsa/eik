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
package org.apache.karaf.eik.core;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public enum SystemBundleNames {
    EQUINOX("org.eclipse.osgi"), FELIX("org.apache.felix.framework");

    private final String symbolicName;

    /**
     * Enumeration constructor that accepts the symbolic name of the OSGi
     * bundles that this plugin understands
     *
     * @param name the symbolic name of the system bundle
     */
    SystemBundleNames(String name) {
        this.symbolicName = name;
    }

    @Override
    public String toString() {
        return this.symbolicName;
    }
}
