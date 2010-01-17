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
package info.evanchik.eclipse.karaf.core.jmx;

import java.io.Serializable;
import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
final public class MBeanServerConnectionDescriptor implements Serializable {

    private static final long serialVersionUID = 2247959701749762077L;

    /**
     * Creates a description of a connection to an MBeanServer using the
     * hostname and port instead of a traditional URL
     *
     * @param id
     *            a unique identifier for this connection descriptor
     * @param hostname
     *            the hostname or IP address to connect to
     * @param port
     *            the port that the MBeanServer is listening on
     * @param username
     *            the username or null if authentication is not necessary
     * @param password
     *            the password or null if authentication is not necessary
     * @return
     * @throws MalformedURLException
     */
    public static MBeanServerConnectionDescriptor createStandardDescriptor(String id,
                    String hostname, int port, String username, String password) throws MalformedURLException {
        final StringBuilder sb = new StringBuilder();

        // Build up something like:
        // service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
        sb.append("service:jmx:rmi:///jndi/rmi://");
        sb.append(hostname);
        sb.append(":");
        sb.append(new Integer(port).toString());
        sb.append("/jmxrmi");

        return new MBeanServerConnectionDescriptor(id, sb.toString(), username, password);
    }

    private final String id;

    private final String username;

    private final String password;

    /**
     * The {@link JMXServiceURL} of the JMX server
     */
    private final JMXServiceURL jmxServiceUrl;

    /**
     * Constructor that creates a MBeanServer connection description using a URL
     * as the primary means of connecting.
     *
     * @param id
     *            a unique identifier for this connection descriptor
     * @param url
     *            a URL to the MBeanServer, similar to {@code
     *            service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi}
     * @param username
     *            the username or null if authentication is not necessary
     * @param password
     *            the password or null if authentication is not necessary
     * @throws MalformedURLException
     */
    public MBeanServerConnectionDescriptor(String id, String url, String username, String password) throws MalformedURLException {
        this.id = id;
        this.username = username;
        this.password = password;

        this.jmxServiceUrl = new JMXServiceURL(url);
    }

    public String getId() {
        return id;
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
}
