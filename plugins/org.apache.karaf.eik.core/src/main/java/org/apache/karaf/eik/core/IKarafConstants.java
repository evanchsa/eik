/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.core;

public interface IKarafConstants {

    /**
     * Karaf base directory, used to reference core Karaf system files; takes
     * precedence over Karaf home directory
     */
    public static final String KARAF_BASE_PROP = "karaf.base";

    public static final String KARAF_BUNDLE_LOCATIONS_PROP = "bundle.locations";

    public static final String KARAF_DATA_PROP = "karaf.data";

    public static final String KARAF_ETC_PROP = "karaf.etc";

    public static final String KARAF_DEFAULT_BUNDLE_START_LEVEL = "80";

    public static final String KARAF_DEFAULT_CONFIG_PROPERTIES_FILE = "config.properties";

    public static final String KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME = "org.apache.karaf.eik.target";

    public static final String KARAF_DEFAULT_STARTUP_PROPERTIES_FILE = "startup.properties";

    public static final String KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE = "system.properties";

    /**
     * Karaf home directory, used to reference installation specific files
     */
    public static final String KARAF_HOME_PROP = "karaf.home";

    public static final String KARAF_INSTANCES_PROP = "karaf.instances";

    public static final String KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME = "org.apache.karaf.jaas.boot";

    public static final String KARAF_MAIN_BUNDLE_SYMBOLIC_NAME = "org.apache.karaf.main";

    public static final String KARAF_OBR_REPOSITORY_PROP = "obr.repository.url";

    public static final String KARAF_OSGI_FRAMEWORK_ID = "org.apache.karaf.eik.Framework";

    public static final String ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME = "org.apache.karaf.management.cfg";

    public static final String ORG_APACHE_KARAF_FEATURES_CFG_FILENAME = "org.apache.karaf.features.cfg";

}
