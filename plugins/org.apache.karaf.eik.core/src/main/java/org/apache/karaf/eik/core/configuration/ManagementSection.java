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

import java.net.URL;

public interface ManagementSection extends ConfigurationSection {

    /**
     * Getter for the JMX MBeanServer port
     *
     * @return the JXM MBeanServer port
     */
    public int getPort();

    /**
     * The JMX Realm
     *
     * @return the JMX realm
     */
    public String getRealm();

    /**
     * The complete {@link URL} to the JMX MBeanServer
     *
     * @return the {@link URL} to the JMX MBeanServer
     */
    public URL getUrl();

    /**
     * Setter for the port that the JMX MBeanServer will listen on
     *
     * @param port
     *            the JMX MBeanServer port
     */
    public void setPort(int port);

}
