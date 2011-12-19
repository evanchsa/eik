/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.ui.configuration;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractConfigurationSection implements ConfigurationSection {

    private final String configSectionId;

    private final IPath configFilename;

    /**
     * The parent of this configuration section is used to get implementation
     * specific items such as the path to a configuration file.
     */
    private final KarafPlatformModel parent;

    private final IKarafProject karafProject;

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
    public AbstractConfigurationSection(final String id, final IPath filename, final KarafPlatformModel parent) {
        this.configSectionId = id;
        this.configFilename = filename;
        this.parent = parent;
        this.karafProject = (IKarafProject) parent.getAdapter(IKarafProject.class);
    }

    @Override
    public IPath getFilename() {
        return configFilename;
    }

    @Override
    public String getId() {
        return configSectionId;
    }

    @Override
    public KarafPlatformModel getParent() {
        return parent;
    }

    public IKarafProject getKarafProject() {
        return karafProject;
    }
}
