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
package org.apache.karaf.eik.workbench.jmx;

import org.apache.karaf.eik.core.KarafPlatformModel;

import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;

public class LocalJMXServiceDescriptor extends JMXServiceDescriptor {

    private static final long serialVersionUID = 1L;

    private final KarafPlatformModel karafPlatformModel;

    public LocalJMXServiceDescriptor(
            final String name,
            final KarafPlatformModel karafPlatformModel,
            final JMXServiceURL url,
            final String username,
            final String password,
            final String domain)
    {
        super(name, url, username, password, domain);

        this.karafPlatformModel = karafPlatformModel;
    }

    public LocalJMXServiceDescriptor(
            final String name,
            final KarafPlatformModel karafPlatformModel,
            final String url,
            final String username,
            final String password,
            final String domain)
        throws MalformedURLException
    {
        super(name, url, username, password, domain);

        this.karafPlatformModel = karafPlatformModel;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof LocalJMXServiceDescriptor)) {
            return false;
        }

        final LocalJMXServiceDescriptor other = (LocalJMXServiceDescriptor) obj;
        if (karafPlatformModel == null) {
            if (other.karafPlatformModel != null) {
                return false;
            }
        } else if (!karafPlatformModel.equals(other.karafPlatformModel)) {
            return false;
        }

        return true;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
        if (KarafPlatformModel.class.equals(adapter)) {
            return karafPlatformModel;
        }

        return super.getAdapter(adapter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (karafPlatformModel == null ? 0 : karafPlatformModel.hashCode());
        return result;
    }

}
