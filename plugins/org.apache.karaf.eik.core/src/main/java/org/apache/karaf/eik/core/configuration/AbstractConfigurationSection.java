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

public abstract class AbstractConfigurationSection implements ConfigurationSection {

    private final String configSectionId;

    private final String configFilename;

    /**
     * The parent of this configuration section is used to get implementation
     * specific items such as the path to a configuration file.
     */
    private final KarafPlatformModel parent;

    /**
     * Constructor that forces the use of configuration section identifiers and
     * configuration filenames.
     *
     * @param id
     *            the identifier, in reverse domain notation, of this
     *            configuration section
     * @param filename
     *            The name of the file where these configuration items are
     *            typically stored
     * @param parent
     *            the parent model of this configuration section
     */
    public AbstractConfigurationSection(String id, String filename, KarafPlatformModel parent) {
        this.configSectionId = id;
        this.configFilename = filename;
        this.parent = parent;
    }

    public String getFilename() {
        return configFilename;
    }

    public String getId() {
        return configSectionId;
    }

    public KarafPlatformModel getParent() {
        return parent;
    }

}
