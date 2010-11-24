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
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.workbench.provider.OSGiServiceWrapper;

import java.util.Collections;
import java.util.Map;

import org.apache.aries.jmx.codec.BundleData;
import org.apache.aries.jmx.codec.ServiceData;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafOSGiServiceWrapper implements OSGiServiceWrapper {

    private final ServiceData delegate;

    private final BundleData bundle;

    private final Map<String, Object> properties;

    /**
     * Constructs a fully materialized service entry from an {@link ServiceData}
     * its {@link BundleData} and the service's properties as retrieved from the
     * runtime.
     *
     * @param delegate
     *            the delegate {@code ServiceData}
     * @param bundle
     *            the service's bundle in the form of a {@code BundleData}
     * @param properties
     *            the service's properties
     */
    public KarafOSGiServiceWrapper(ServiceData delegate, BundleData bundle, Map<String, Object> properties) {
        this.delegate = delegate;
        this.bundle = bundle;
        this.properties = properties;
    }

    public ServiceData getOSGiService() {
        return delegate;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public BundleData getBundle() {
        return bundle;
    }
}
