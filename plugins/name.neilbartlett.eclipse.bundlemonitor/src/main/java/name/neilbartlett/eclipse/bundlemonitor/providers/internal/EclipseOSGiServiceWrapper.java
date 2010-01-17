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

import info.evanchik.eclipse.karaf.ui.provider.OSGiServiceWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.jmx.codec.OSGiService;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class EclipseOSGiServiceWrapper implements OSGiServiceWrapper {

    private final OSGiBundle bundle;

    private final Map<String, Object> properties;

    private final ServiceReference reference;

    private final OSGiService serviceDelegate;

    public EclipseOSGiServiceWrapper(ServiceReference reference, OSGiBundle bundle) {
        this.reference = reference;

        this.serviceDelegate = new OSGiService(reference);

        this.bundle = bundle;

        this.properties = new HashMap<String, Object>();
    }

    public OSGiBundle getBundle() {
        final Bundle b = reference.getBundle();
        if (b == null) {
            return null; // UnregisteredServiceItem.getInstance().getBundle();
        } else {
            return bundle;
        }

    }

    public OSGiService getOSGiService() {
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
