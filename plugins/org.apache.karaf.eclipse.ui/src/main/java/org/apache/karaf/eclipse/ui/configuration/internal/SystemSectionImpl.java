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
import org.apache.karaf.eclipse.ui.configuration.SystemSection;
import org.eclipse.core.runtime.Path;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class SystemSectionImpl extends AbstractPropertiesConfigurationSection implements
                SystemSection {

    public static String SYSTEM_SECTION_ID = "org.apache.karaf.eclipse.configuration.section.System";

    public static String SYSTEM_FILENAME = "system.properties";

    /**
     * @param parent
     */
    public SystemSectionImpl(final KarafPlatformModel parent) {
        super(SYSTEM_SECTION_ID, new Path("etc").append(SYSTEM_FILENAME), parent);
    }

    @Override
    public String getProperty(final String key) {
        return getProperties().getProperty(key);
    }

    @Override
    public void setProperty(final String key, final String value) {
        getProperties().setProperty(key, value);
    }
}
