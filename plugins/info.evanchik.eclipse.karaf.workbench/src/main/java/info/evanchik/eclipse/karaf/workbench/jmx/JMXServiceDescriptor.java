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
package info.evanchik.eclipse.karaf.workbench.jmx;

import java.io.Serializable;
import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;

/**
 * A {@code JMXServiceDescriptor} contains all of the elements necessary to
 * describe a connection to a JMX endpoint:<br>
 * <br>
 * <ol>
 * <li>A human readable name - Suitable for displaying via the UI</li>
 * <li>A {@link JMXServiceURL} - Used to connect to the MBeanServer</li>
 * <li>Credentials - A username and password credential pair</li>
 * </ol>
 * <br>
 * These elements form the necessary descriptive information to use in the JMX
 * Client UI.
 *
 * @see JMXServiceURL
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class JMXServiceDescriptor implements Serializable {

    public static final String DEFAULT_DOMAIN = "jmxserver"; //$NON-NLS-1$

    public static final String DEFAULT_PORT = "8118"; //$NON-NLS-1$

    public static final String DEFAULT_PROTOCOL = "rmi"; //$NON-NLS-1$

    public static final int DEFAULT_PORT_AS_INT = Integer.parseInt(DEFAULT_PORT);

    private static final long serialVersionUID = 1L;

	/**
	 * Gets the {@code JMXServiceDescriptor} for the local MBean server
	 *
	 * @param username
	 *            the username or null if authentication is not necessary
	 * @param password
	 *            the password or null if authentication is not necessary
	 * @return
	 */
    public static JMXServiceDescriptor getLocalJMXServiceDescriptor(final String username, final String password) {
    	try {
    		final JMXServiceURL url =
    			new JMXServiceURL(
    					DEFAULT_PROTOCOL,
    					"localhost", //$NON-NLS-1$
    					DEFAULT_PORT_AS_INT,
    					"/" + DEFAULT_DOMAIN); //$NON-NLS-1$

    		return new JMXServiceDescriptor("Local JMX Service", url, username, password, DEFAULT_DOMAIN);
    	} catch(final MalformedURLException e) {
    		// This is a programming error
    		throw new AssertionError(e);
    	}
    }

    private final String name;

    private final String username;

    private final String password;

    private final String domain;

    /**
     * The {@link JMXServiceURL} of the JMX server
     */
    private final JMXServiceURL jmxServiceUrl;

    /**
     * Constructor that creates a {@link JMXServiceURL} connection description
     * using a preexisting {@code JMXServiceURL} instance.
     *
     * @param name
     *            the name for this connection descriptor
     * @param url
     *            the {@code JMXServiceURL} to use
     * @param username
     *            the username or null if authentication is not necessary
     * @param password
     *            the password or null if authentication is not necessary
     * @param domain
     *            the JMX domain used in {@code url}
     */
    public JMXServiceDescriptor(
    		final String name,
    		final JMXServiceURL url,
    		final String username,
    		final String password,
    		final String domain)
	{
        this.name = name;
        this.username = username;
        this.password = password;
        this.domain = domain;

        this.jmxServiceUrl = url;
    }

	/**
	 * Constructor that creates a {@link JMXServiceURL} connection description
	 * using a String URL as the primary means of connecting.
	 *
	 * @param name
	 *            the name for this connection descriptor
	 * @param url
	 *            a URL to the MBeanServer, similar to {@code
	 *            service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi}
	 * @param username
	 *            the username or null if authentication is not necessary
	 * @param password
	 *            the password or null if authentication is not necessary
     * @param domain
     *            the JMX domain to use
	 * @throws MalformedURLException if the given string URL is not well formed
	 */
    public JMXServiceDescriptor(
    		final String name,
    		final String url,
    		final String username,
    		final String password,
    		final String domain) throws MalformedURLException
	{
    	this(name, new JMXServiceURL(url), username, password, domain);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public JMXServiceURL getUrl() {
        return jmxServiceUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (domain == null ? 0 : domain.hashCode());
        result = prime * result + (jmxServiceUrl == null ? 0 : jmxServiceUrl.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + (username == null ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final JMXServiceDescriptor other = (JMXServiceDescriptor) obj;
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }

        if (jmxServiceUrl == null) {
            if (other.jmxServiceUrl != null) {
                return false;
            }
        } else if (!jmxServiceUrl.equals(other.jmxServiceUrl)) {
            return false;
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }

        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return name + "[" + username + "@" + jmxServiceUrl.toString() + "]";
    }
}
