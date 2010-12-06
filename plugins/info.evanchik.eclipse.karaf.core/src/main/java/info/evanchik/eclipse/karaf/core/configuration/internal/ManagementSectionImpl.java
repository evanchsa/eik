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
package info.evanchik.eclipse.karaf.core.configuration.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.AbstractPropertiesConfigurationSection;
import info.evanchik.eclipse.karaf.core.configuration.ManagementSection;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ManagementSectionImpl extends AbstractPropertiesConfigurationSection implements ManagementSection {

    public static final String MANAGEMENT_SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.Management"; //$NON-NLS-1$

    public static final String SERVICEMIX_KERNEL_MANAGEMENT_FILENAME = "org.apache.servicemix.management.cfg"; //$NON-NLS-1$

    public static final String FELIX_KARAF_MANAGEMENT_FILENAME = "org.apache.felix.karaf.management.cfg"; //$NON-NLS-1$

    public static final String KARAF_MANAGEMENT_FILENAME = "org.apache.karaf.management.cfg"; //$NON-NLS-1$

    /**
     * @param parent
     * @param filename
     */
    public ManagementSectionImpl(KarafPlatformModel parent, String filename) {
        super(MANAGEMENT_SECTION_ID, filename, parent);
    }

    public int getPort() {
        final String portString = getProperties().getProperty("rmiRegistryPort"); //$NON-NLS-1$
        return new Integer(portString);
    }

    public String getRealm() {
        return getProperties().getProperty("jmxRealm"); //$NON-NLS-1$
    }

    public URL getUrl() {
        try {
            final String url = getProperties().getProperty("serviceUrl"); //$NON-NLS-1$
            return new URL(url);
        } catch (MalformedURLException e) {
        }

        return null;
    }

    public void setPort(int port) {
        getProperties().setProperty("rmiRegistryPort", new Integer(port).toString()); //$NON-NLS-1$
        getProperties().setProperty("serviceUrl", makeJMXUrl(new Integer(port).toString())); //$NON-NLS-1$
    }

    private String makeJMXUrl(String port) {
        final StringBuilder sb = new StringBuilder();

        // Build up something like:
        // service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
        sb.append("service:jmx:rmi:///jndi/rmi://"); //$NON-NLS-1$
        sb.append("localhost"); //$NON-NLS-1$
        sb.append(":"); //$NON-NLS-1$
        sb.append(port);
        sb.append("/jmxrmi"); //$NON-NLS-1$

        return sb.toString();
    }
}
