/**
 * Copyright (c) 2011 Stephen Evanchik
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
import info.evanchik.eclipse.karaf.core.configuration.ShellSection;

import java.io.File;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ShellSectionImpl extends AbstractPropertiesConfigurationSection
        implements ShellSection {

    public static final String FILENAME = "org.apache.karaf.shell.cfg"; // $NON-NLS-1$

    public static final String SECTION_ID = "info.evanchik.eclipse.karaf.configuration.section.Shell"; //$NON-NLS-1$

    public ShellSectionImpl(final KarafPlatformModel parent) {
        super(SECTION_ID, FILENAME, parent);
    }

    @Override
    public Integer getSshPort() {
        final String portString = getProperties().getProperty("sshPort"); //$NON-NLS-1$
        return new Integer(portString);
    }

    @Override
    public String getSshHost() {
        return getProperties().getProperty("sshHost"); //$NON-NLS-1$;
    }

    @Override
    public String getSshRealm() {
        return getProperties().getProperty("sshRealm"); //$NON-NLS-1$;
    }

    @Override
    public File getHostKey() {
        final String fileString = getProperties().getProperty("hostKey"); //$NON-NLS-1$;
        return new File(fileString);
    }
}
