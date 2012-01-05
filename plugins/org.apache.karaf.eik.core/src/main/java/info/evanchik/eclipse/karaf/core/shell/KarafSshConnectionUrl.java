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
 * This object represents the necessary information for a Karaf
 * SSH connection
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafSshConnectionUrl {

    private final String host;

    private final int port;

    /**
     * This object represents the necessary information for a Karaf
     * SSH connection
     *
     * @param host
     *            the remote host
     * @param port
     *            the port of the remote host
     */
    public KarafSshConnectionUrl(final String host, final int port) {
        this.host = host;
        this.port = port;
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

        if (port != other.port) {
            return false;
        }

        return true;
    }

    /**
     * Getter for the remote host
     *
     * @return the remote host
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter for the SSH server's port
     *
     * @return the SSH server's port
     */
    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (host == null ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }


}