/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.ui.configuration.internal;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eclipse.ui.configuration.ManagementSection;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ManagementSectionImpl extends AbstractPropertiesConfigurationSection implements ManagementSection {

    public static final String MANAGEMENT_SECTION_ID = "org.apache.karaf.eclipse.configuration.section.Management"; //$NON-NLS-1$

    /**
     * @param parent
     * @param filename
     */
    public ManagementSectionImpl(final KarafPlatformModel parent, final String filename) {
        super(MANAGEMENT_SECTION_ID, filename, parent);
    }

    @Override
    public int getPort() {
        final String portString = getProperties().getProperty("rmiRegistryPort"); //$NON-NLS-1$
        return new Integer(portString);
    }

    @Override
    public String getRealm() {
        return getProperties().getProperty("jmxRealm"); //$NON-NLS-1$
    }

    @Override
    public URL getUrl() {
        try {
            final String url = getProperties().getProperty("serviceUrl"); //$NON-NLS-1$
            return new URL(url);
        } catch (final MalformedURLException e) {
        }

        return null;
    }

    @Override
    public void setPort(final int port) {
        getProperties().setProperty("rmiRegistryPort", new Integer(port).toString()); //$NON-NLS-1$
        getProperties().setProperty("serviceUrl", makeJMXUrl(new Integer(port).toString())); //$NON-NLS-1$
    }

    private String makeJMXUrl(final String port) {
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
