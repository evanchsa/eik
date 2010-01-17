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
package info.evanchik.eclipse.karaf.ui.provider.internal;

import info.evanchik.eclipse.karaf.ui.provider.OSGiServiceWrapper;

import java.util.Collections;
import java.util.Map;

import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.jmx.codec.OSGiService;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafOSGiServiceWrapper implements OSGiServiceWrapper {

    private final OSGiService delegate;

    private final OSGiBundle bundle;

    private final Map<String, Object> properties;

    /**
     * Constructs a fully materialized service entry from an {@link OSGiService}
     * its {@link OSGiBundle} and the service's properties as retrieved from the
     * runtime.
     *
     * @param delegate
     *            the delegate {@code OSGiService}
     * @param bundle
     *            the service's bundle in the form of a {@code OSGiBundle}
     * @param properties
     *            the service's properties
     */
    public KarafOSGiServiceWrapper(OSGiService delegate, OSGiBundle bundle, Map<String, Object> properties) {
        this.delegate = delegate;
        this.bundle = bundle;
        this.properties = properties;
    }

    public OSGiService getOSGiService() {
        return delegate;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public OSGiBundle getBundle() {
        return bundle;
    }
}
