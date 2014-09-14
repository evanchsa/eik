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
package org.apache.karaf.eik.core.configuration;

import java.io.File;

public interface ShellSection extends ConfigurationSection {

    /**
     * Getter for the network port that the SSH server listens on
     *
     * @return the network port of the SSH server
     */
    Integer getSshPort();

    /**
     * Getter for the host address that the SSH server will bind to. This can be
     * {@code 0.0.0.0} which means the SSH server will bind to all addresses
     *
     * @return the host address that the SSH server will bind to.
     */
    String getSshHost();

    /**
     * Getter for the JAAS domain to use for password authentication
     *
     * @return the JAAS domain to use for password authentication
     */
    String getSshRealm();

    /**
     * Getter for the location of the {@link File} that defines the SSH server's
     * public/private key pair
     *
     * @return the location of the {@code File} that defines the SSH server's
     *         public/private key pair.
     */
    File getHostKey();

}
