/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.jmx;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;

import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class LocalJMXServiceDescriptor extends JMXServiceDescriptor {

    private static final long serialVersionUID = 1L;

    private final KarafPlatformModel karafPlatformModel;

    /**
     * @param name
     * @param karafPlatformModel
     * @param url
     * @param username
     * @param password
     * @param domain
     */
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

    /**
     * @param name
     * @param karafPlatformModel
     * @param url
     * @param username
     * @param password
     * @param domain
     * @throws MalformedURLException
     */
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
