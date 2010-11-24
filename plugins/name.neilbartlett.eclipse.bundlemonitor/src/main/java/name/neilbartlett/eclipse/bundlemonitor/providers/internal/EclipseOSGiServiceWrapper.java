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
package name.neilbartlett.eclipse.bundlemonitor.providers.internal;

import info.evanchik.eclipse.karaf.workbench.provider.OSGiServiceWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.aries.jmx.codec.BundleData;
import org.apache.aries.jmx.codec.ServiceData;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class EclipseOSGiServiceWrapper implements OSGiServiceWrapper {

    private final BundleData bundle;

    private final Map<String, Object> properties;

    private final ServiceReference reference;

    private final ServiceData serviceDelegate;

    public EclipseOSGiServiceWrapper(ServiceReference reference, BundleData bundle) {
        this.reference = reference;

        this.serviceDelegate = new ServiceData(reference);

        this.bundle = bundle;

        this.properties = new HashMap<String, Object>();
    }

    public BundleData getBundle() {
        final Bundle b = reference.getBundle();
        if (b == null) {
            return null; // UnregisteredServiceItem.getInstance().getBundle();
        } else {
            return bundle;
        }

    }

    public ServiceData getOSGiService() {
        return serviceDelegate;
    }

    public Map<String, Object> getProperties() {
        properties.clear();

        for (String key : reference.getPropertyKeys()) {
            properties.put(key, reference.getProperty(key));
        }

        return Collections.unmodifiableMap(properties);
    }
}
