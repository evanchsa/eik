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

import org.apache.karaf.eik.core.internal.KarafCorePluginActivator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.common.util.NoCloseInputStream;
import org.apache.sshd.common.util.NoCloseOutputStream;
import org.fusesource.jansi.AnsiConsole;

public class KarafSshShellConnection implements KarafRemoteShellConnection {

    public static final class Credentials {

        private final String password;

        private final String username;

        public Credentials(final String username, final String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof Credentials)) {
                return false;
            }

            final Credentials other = (Credentials) obj;
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

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (password == null ? 0 : password.hashCode());
            result = prime * result + (username == null ? 0 : username.hashCode());
            return result;
        }
    }

    private ClientChannel clientChannel;

    private ClientSession clientSession;

    private final AtomicBoolean connected = new AtomicBoolean();

    private int connectionStatus;

    private final KarafSshConnectionUrl connectionUrl;

    private final Credentials credentials;

    private final OutputStream errorStream;

    private volatile Throwable exception;

    private final InputStream inputStream;

    private final OutputStream outputStream;

    private SshClient sshClient;

    /**
     * Creates a remote shell connection to a Karaf instance using the SSH
     * protocol
     *
     * @param connectionUrl
     *            the coordinates used to establish the connection
     * @param credentials
     *            the username and password used to authenticate when connection
     * @param inputStream
     *            the {@link InputStream} used to interact with the remote
     *            system
     * @param outputStream
     *            the {@link OutputStream} used to receive data from
     *            {@code stdout} of the remote system
     * @param errorStream
     *            the {@link OutputStream} used to receive data from
     *            {@code stderr} of the remote system
     */
    public KarafSshShellConnection(
            final KarafSshConnectionUrl connectionUrl,
            final Credentials credentials,
            final InputStream inputStream,
            final OutputStream outputStream,
            final OutputStream errorStream)
    {

        this.connectionUrl = connectionUrl;
        this.credentials = credentials;

        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;

        this.exception = null;

        this.sshClient = SshClient.setUpDefaultClient();
    }

    @Override
    public void connect() throws IOException {
        sshClient.start();

        try {
            final ConnectFuture connectFuture = sshClient.connect(connectionUrl.getHost(), connectionUrl.getPort());
            connectFuture.await(15 * 1000);

            clientSession = connectFuture.getSession();

            final AuthFuture authFuture = clientSession.authPassword(credentials.getUsername(), credentials.getPassword());
            authFuture.await(15 * 1000);

            if (!authFuture.isSuccess()) {
                exception = authFuture.getException();

                try {
                    disconnect();
                } catch (final IOException e) {
                    KarafCorePluginActivator.getLogger().warn("Error while disconnecting after authenticate failure", e);
                }

                throw new IOException(exception);
            }

            clientChannel = clientSession.createChannel(ClientChannel.CHANNEL_SHELL);

            clientChannel.setIn(new NoCloseInputStream(inputStream));
            clientChannel.setOut(AnsiConsole.wrapOutputStream(new NoCloseOutputStream(outputStream)));
            clientChannel.setErr(AnsiConsole.wrapOutputStream(new NoCloseOutputStream(errorStream)));

            connected.set(true);

            clientChannel.open();
            connectionStatus = clientChannel.waitFor(ClientChannel.CLOSED, 15 * 1000);
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            if (clientChannel != null) {
                clientChannel.close(true).await();
            }

            if (clientSession != null) {
                clientSession.close(true).await();
            }
        } catch (final Exception e) {
            throw new IOException(e);
        } finally {
            clientChannel = null;
            clientSession = null;

            connected.set(false);
        }
    }

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    public void setSshClient(final SshClient sshClient) {
        this.sshClient = sshClient;
    }

}
