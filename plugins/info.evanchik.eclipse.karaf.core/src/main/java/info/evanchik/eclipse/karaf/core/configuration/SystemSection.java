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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface SystemSection extends ConfigurationSection {

    /**
     * Getter for a System property that is set during Karaf execution
     *
     * @param key
     *            the key of the System property
     * @return the value of the System property
     */
    public String getProperty(String key);

    /**
     * Setter for a System property that is set during Karaf execution
     *
     * @param key
     *            the key of the System property
     * @param value
     *            the value of the System property
     */
    public void setProperty(String key, String value);
}
