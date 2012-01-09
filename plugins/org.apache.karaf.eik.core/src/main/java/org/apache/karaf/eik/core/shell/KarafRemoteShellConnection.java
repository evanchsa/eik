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

import java.io.IOException;

public interface KarafRemoteShellConnection {

    /**
     * Connects to the remote shell of a Karaf instance.
     * This method will block until the connection has been established.
     */
    public void connect() throws IOException;

    /**
     * Disconnects from the remote shell of a Karaf instance.
     * This method will block until the connection has been severed
     */
    public void disconnect() throws IOException;

    /**
     * Determines whether or not there is a connection to the Karaf remote shell
     *
     * @return true if a connection exists, false otherwise
     */
    public boolean isConnected();

}
