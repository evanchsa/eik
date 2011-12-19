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
package org.apache.karaf.eclipse.ui.configuration.internal;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.configuration.AbstractPropertiesConfigurationSection;
import org.apache.karaf.eclipse.ui.configuration.ShellSection;

import java.io.File;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ShellSectionImpl extends AbstractPropertiesConfigurationSection
        implements ShellSection {

    public static final String FILENAME = "org.apache.karaf.shell.cfg"; // $NON-NLS-1$

    public static final String SECTION_ID = "org.apache.karaf.eclipse.configuration.section.Shell"; //$NON-NLS-1$

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
