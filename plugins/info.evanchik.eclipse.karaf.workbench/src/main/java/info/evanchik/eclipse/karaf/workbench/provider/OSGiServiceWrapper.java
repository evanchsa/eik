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

import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.jmx.codec.OSGiService;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface OSGiServiceWrapper {

    /**
     * Getter for the underlying {@link OSGiService} delegate
     *
     * @return the instance of the {@code OSGiService}
     */
    public OSGiService getOSGiService();

    /**
     * Getter for the {@link OSGiBundle} that owns this {@link OSGiService}
     *
     * @return the instance of the {@code OSGiBundle} that owns this {@code
     *         OSGiService}
     */
    public OSGiBundle getBundle();

    /**
     * Getter for the properties of this {@link OSGiService}
     *
     * @return the properties of this {@code OSGiService}
     */
    public Map<String, Object> getProperties();
}
