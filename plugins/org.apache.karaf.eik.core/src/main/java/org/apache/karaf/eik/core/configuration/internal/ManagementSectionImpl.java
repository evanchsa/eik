/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.core.configuration.internal;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eik.core.configuration.ManagementSection;

import java.net.MalformedURLException;
import java.net.URL;

public class ManagementSectionImpl extends AbstractPropertiesConfigurationSection implements ManagementSection {

    public static final String MANAGEMENT_SECTION_ID = "org.apache.karaf.eik.configuration.section.Management";

    /**
     * @param parent
     * @param filename
     */
    public ManagementSectionImpl(final KarafPlatformModel parent, final String filename) {
        super(MANAGEMENT_SECTION_ID, filename, parent);
    }

    @Override
    public int getPort() {
        final String portString = getProperties().getProperty("rmiRegistryPort");
        return new Integer(portString);
    }

    @Override
    public String getRealm() {
        return getProperties().getProperty("jmxRealm");
    }

    @Override
    public URL getUrl() {
        try {
            final String url = getProperties().getProperty("serviceUrl");
            return new URL(url);
        } catch (final MalformedURLException e) {
        }

        return null;
    }

    @Override
    public void setPort(final int port) {
        getProperties().setProperty("rmiRegistryPort", new Integer(port).toString());
        getProperties().setProperty("serviceUrl", makeJMXUrl(new Integer(port).toString()));
    }

    private String makeJMXUrl(final String port) {
        final StringBuilder sb = new StringBuilder();

        // Build up something like:
        // service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
        sb.append("service:jmx:rmi:///jndi/rmi://");
        sb.append("localhost");
        sb.append(":");
        sb.append(port);
        sb.append("/jmxrmi");

        return sb.toString();
    }

}
