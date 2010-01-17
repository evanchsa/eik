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
package info.evanchik.eclipse.karaf.core.configuration;

import java.net.URL;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface ManagementSection extends ConfigurationSection {

    /**
     * Getter for the JMX MBeanServer port
     *
     * @return the JXM MBeanServer port
     */
    public int getPort();

    /**
     * The JMX Realm
     *
     * @return the JMX realm
     */
    public String getRealm();

    /**
     * The complete {@link URL} to the JMX MBeanServer
     *
     * @return the {@link URL} to the JMX MBeanServer
     */
    public URL getUrl();

    /**
     * Setter for the port that the JMX MBeanServer will listen on
     *
     * @param port
     *            the JMX MBeanServer port
     */
    public void setPort(int port);
}
