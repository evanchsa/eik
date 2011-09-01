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
