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

import org.eclipse.core.runtime.IStatus;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
