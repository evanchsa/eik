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
import info.evanchik.eclipse.karaf.core.configuration.GeneralSection;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GeneralSectionImpl extends AbstractPropertiesConfigurationSection implements GeneralSection {

    public static final String GENERAL_SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.General";

    public static final String GENERAL_FILENAME = "config.properties";

    /**
     * @param parent
     */
    public GeneralSectionImpl(KarafPlatformModel parent) {
        super(GENERAL_SECTION_ID, GENERAL_FILENAME, parent);
    }

}
