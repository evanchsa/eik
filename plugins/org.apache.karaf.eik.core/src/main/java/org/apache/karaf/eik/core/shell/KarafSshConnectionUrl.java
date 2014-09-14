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
package org.apache.karaf.eik.core.shell;

/**
 * This object represents the necessary information for a Karaf
 * SSH connection.
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