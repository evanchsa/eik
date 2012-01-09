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

import org.apache.karaf.eik.core.KarafPlatformModel;

import org.eclipse.core.runtime.IStatus;

public interface ConfigurationSection {

    /**
     * The name of the file where these configuration items are typically stored
     *
     * @return the name of the file that backs this configuration section
     */
    public String getFilename();

    /**
     * Getter for the identifier of this configuration section.
     *
     * @return the identifier, in reverse domain notation, of this configuration
     *         section.
     */
    public String getId();

    /**
     * Getter for the parent {@link KarafTargetPlatform}
     *
     * @return a {@link KarafTargetPlatform}
     */
    public KarafPlatformModel getParent();

    /**
     * Loads the configuration data for this section
     */
    public IStatus load();

    /**
     * Saves the configuration data for this section
     */
    public IStatus save();

}
