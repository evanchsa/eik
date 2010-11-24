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
package info.evanchik.eclipse.karaf.workbench.provider;

import java.util.Map;

import org.apache.aries.jmx.codec.BundleData;
import org.apache.aries.jmx.codec.ServiceData;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface OSGiServiceWrapper {

    /**
     * Getter for the underlying {@link ServiceData} delegate
     *
     * @return the instance of the {@code ServiceData}
     */
    public ServiceData getOSGiService();

    /**
     * Getter for the {@link BundleData} that owns this {@link ServiceData}
     *
     * @return the instance of the {@code BundleData} that owns this {@code
     *         ServiceData}
     */
    public BundleData getBundle();

    /**
     * Getter for the properties of this {@link ServiceData}
     *
     * @return the properties of this {@code ServiceData}
     */
    public Map<String, Object> getProperties();
}
