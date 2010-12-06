/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.configuration;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
