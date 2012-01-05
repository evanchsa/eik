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
package info.evanchik.eclipse.karaf.core.configuration.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.AbstractPropertiesConfigurationSection;
import info.evanchik.eclipse.karaf.core.configuration.SystemSection;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class SystemSectionImpl extends AbstractPropertiesConfigurationSection implements
                SystemSection {

    public static String SYSTEM_SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.System";

    public static String SYSTEM_FILENAME = "system.properties";

    /**
     * @param parent
     */
    public SystemSectionImpl(KarafPlatformModel parent) {
        super(SYSTEM_SECTION_ID, SYSTEM_FILENAME, parent);
    }

    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    public void setProperty(String key, String value) {
        getProperties().setProperty(key, value);
    }
}
