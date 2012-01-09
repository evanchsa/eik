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

public interface StartupSection extends ConfigurationSection {

    /**
     * Determines if the specified bundle will be started by Karaf during its
     * initialization.
     *
     * @param bundleSymbolicName
     *            the symbolic name of the bundle
     * @return true if the plugin is listed in the startup configuration for
     *         Karaf, false otherwise
     */
    public boolean containsPlugin(String bundleSymbolicName);

    /**
     * Getter for the start level of the bundle specified by the symbolic name.
     *
     * @param bundleSymbolicName
     *            the symbolic name of the bundle
     * @return the start level of the bundle, null if the bundle does not exist
     *         in the system startup configuration.
     */
    public String getStartLevel(String bundleSymbolicName);

}
