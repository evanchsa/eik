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
package info.evanchik.eclipse.karaf.core.shell;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafSshConnectionUrl {

    private final String host;

    private final String password;

    private final int port;

    private final String username;

    /**
     * This object represents the necessary connection information for a Karaf
     * SSH connection
     *
     * @param host
     *            the remote host
     * @param port
     *            the port of the remote host
     * @param username
     *            the username
     * @param password
     *            the password
     */
    public KarafSshConnectionUrl(final String host, final int port, final String username, final String password) {
        this.host = host;
        this.port = port;

        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof KarafSshConnectionUrl)) {
            return false;
        }

        final KarafSshConnectionUrl other = (KarafSshConnectionUrl) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }

        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }

        if (port != other.port) {
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

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (host == null ? 0 : host.hashCode());
        result = prime * result + (password == null ? 0 : password.hashCode());
        result = prime * result + port;
        result = prime * result + (username == null ? 0 : username.hashCode());
        return result;
    }


}